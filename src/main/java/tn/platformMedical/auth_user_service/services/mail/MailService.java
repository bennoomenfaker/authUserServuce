package tn.platformMedical.auth_user_service.services.mail;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import lombok.AllArgsConstructor;
import tn.platformMedical.auth_user_service.dto.request.PasswordResetConfirmation;
import tn.platformMedical.auth_user_service.dto.request.PasswordResetRequest;
import tn.platformMedical.auth_user_service.exception.InvalidTokenException;
import tn.platformMedical.auth_user_service.exception.TokenExpiredException;
import tn.platformMedical.auth_user_service.models.User;
import tn.platformMedical.auth_user_service.repository.UserRepository;
import tn.platformMedical.auth_user_service.services.kafka.KafkaProducerService;

@Service
@AllArgsConstructor
public class MailService {

    private final UserRepository userRepository;
    //private static final String EMAIL_SERVICE_URL = "http://localhost:9999/mail-service/sendPasswordResetEmail";
   // private static final String EMAIL_CONFIRMATION_URL = "http://localhost:9999/mail-service/confirmPasswordReset";

    private final RestTemplate restTemplate;

    private final KafkaProducerService kafkaProducerService;

    private final PasswordEncoder passwordEncoder;






    public void requestPasswordReset(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            String token = UUID.randomUUID().toString();
            LocalDateTime expirationDate = LocalDateTime.now().plusHours(1);
            user.setResetPasswordToken(token);
            user.setResetPasswordTokenExpired(expirationDate);
            userRepository.save(user);

            PasswordResetRequest passwordResetRequest = new PasswordResetRequest(
                    token, expirationDate, email, user.getFirstName(), user.getLastName()
            );

           // restTemplate.postForObject(EMAIL_SERVICE_URL, passwordResetRequest, String.class);
            kafkaProducerService.sendPasswordResetEvent("password-reset-topic", passwordResetRequest);
        } else {
            throw new RuntimeException("User not found with email: " + email);
        }
    }

    public void resetPassword(String token, String newPassword) {
        Optional<User> optionalUser = userRepository.findByResetPasswordToken(token);

        if (!optionalUser.isPresent()) {
            throw new InvalidTokenException("Token invalide");
        }

        User user = optionalUser.get();

        if (user.getResetPasswordTokenExpired().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("Token expiré");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpired(null);
        userRepository.save(user);

        PasswordResetConfirmation confirmation = new PasswordResetConfirmation(
                user.getEmail(), user.getFirstName(), user.getLastName(),
                "Votre mot de passe a été réinitialisé avec succès."
        );

       // restTemplate.postForObject(EMAIL_CONFIRMATION_URL, confirmation, String.class);

        kafkaProducerService.sendPasswordResetConfirmationEvent("password-reset-confirmation-topic", confirmation);
    }
}

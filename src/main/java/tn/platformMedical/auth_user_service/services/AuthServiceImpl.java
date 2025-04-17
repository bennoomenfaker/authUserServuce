package tn.platformMedical.auth_user_service.services;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tn.platformMedical.auth_user_service.dto.request.LoginRequest;
import tn.platformMedical.auth_user_service.dto.request.SignupRequest;
import tn.platformMedical.auth_user_service.dto.response.JwtResponse;
import tn.platformMedical.auth_user_service.dto.response.MessageResponse;
import tn.platformMedical.auth_user_service.models.Role;
import tn.platformMedical.auth_user_service.models.User;
import tn.platformMedical.auth_user_service.repository.RoleRepository;
import tn.platformMedical.auth_user_service.repository.UserRepository;
import tn.platformMedical.auth_user_service.security.jwt.JwtUtils;
import tn.platformMedical.auth_user_service.security.services.UserDetailsImpl;

import java.util.Optional;


@Service
@AllArgsConstructor
public class AuthServiceImpl  implements AuthService{
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;// Injecte le gestionnaire d'authentification
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils; // Injecte les utilitaires JWT pour la génération et la validation des tokens


    @Override
    @CircuitBreaker(name = "authService", fallbackMethod = "fallbackRegisterUser")
    @Retry(name = "authService")
    public ResponseEntity<?> registerUser(SignupRequest signUpRequest) {
        // Vérifie si l'email est déjà utilisé
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Erreur : Cet e-mail est déjà utilisé !"));
        }

// Déterminer si l'utilisateur appartient à un hôpital ou au MS
        String hospitalId = signUpRequest.getHospitalId(); // Peut-être null si MS
        String serviceId =  signUpRequest.getServiceId();

        // Récupérer le rôle à partir du nom
        Optional<Role> roleOptional = roleRepository.findByName(signUpRequest.getRole());
        if (!roleOptional.isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Role not found!"));
        }
        Role role = roleOptional.get();
        User user = new User(
                signUpRequest.getFirstName(),
                signUpRequest.getLastName(),
                signUpRequest.getEmail(),
                passwordEncoder.encode(signUpRequest.getPassword()),
                signUpRequest.getTelephone(),
                hospitalId ,
                serviceId,
                role

        );

       User u = userRepository.save(user);
        return ResponseEntity.status(201).body(new MessageResponse("Utilisateur enregistré avec succès!" , u.getId()));
    }


    public ResponseEntity<?> fallbackRegisterUser(SignupRequest signUpRequest, Throwable t) {
        return ResponseEntity.status(503).body(new MessageResponse("Service unavailable. Please try again later."));
    }


    @Override
    @CircuitBreaker(name = "authService", fallbackMethod = "fallbackAuthenticateUser")
    @Retry(name = "authService")
    public ResponseEntity<JwtResponse> authenticateUser(LoginRequest loginRequest) {


        // Authentifie l'utilisateur avec les informations de connexion fournies
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        // Stocke l'authentification dans le contexte de sécurité
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Génère un token JWT pour l'utilisateur authentifié
        String jwt = jwtUtils.generateJwtToken(authentication);

        // Récupère les détails de l'utilisateur
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Role role =  userDetails.getRole();
        // Retourne une réponse contenant le token JWT, les informations de l'utilisateur et son rôle unique
        JwtResponse jwtResponse = new JwtResponse(
                jwt,
                "Bearer",
                userDetails.getId(),
                userDetails.getUsername(),
                role
        );

        return ResponseEntity.ok(jwtResponse);
    }


    public ResponseEntity<MessageResponse> fallbackAuthenticateUser(LoginRequest loginRequest, Throwable t) {
        return ResponseEntity.status(503).body(new MessageResponse("Error: Service unavailable. Please try again later."));
    }

    @Override
    public Boolean existUserByEmail(String email) {
        return userRepository.existsByEmail(email);
    }


    @Override
    public ResponseEntity<?> getProfile(String token) {
        try {
            // Valider le token JWT
            if (!jwtUtils.validateJwtToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token invalide");
            }

            String email = jwtUtils.getUserNameFromJwtToken(token);
            return userRepository.findByEmail(email)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expiré");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Erreur de validation du token");
        }
    }

}
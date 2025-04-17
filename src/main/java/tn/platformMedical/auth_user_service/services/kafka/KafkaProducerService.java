package tn.platformMedical.auth_user_service.services.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tn.platformMedical.auth_user_service.dto.request.PasswordResetConfirmation;
import tn.platformMedical.auth_user_service.dto.request.PasswordResetRequest;

import java.time.Instant;

/**
 * Service Kafka Producer pour envoyer des événements.
 */
@Slf4j
@Service
@AllArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendMessage(String topic, String eventType, String email, String message) {
        String payload = String.format(
                "{\"event\": \"%s\", \"email\": \"%s\", \"message\": \"%s\", \"timestamp\": \"%s\"}",
                eventType, email, message, Instant.now()
        );
        kafkaTemplate.send(topic, payload);
        log.info("Message envoyé au topic {}: {}", topic, payload);
    }


    public void sendPasswordResetEvent(String topic, PasswordResetRequest event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, eventJson);
            log.info("Password reset event envoyé au topic {}: {}", topic, eventJson);
        } catch (Exception e) {
            log.error("Erreur lors de la conversion en JSON de l'événement de réinitialisation du mot de passe : {}", e.getMessage());
        }
    }

    public void sendPasswordResetConfirmationEvent(String topic, PasswordResetConfirmation event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, eventJson);
            log.info("Password reset confirmation event envoyé au topic {}: {}", topic, eventJson);
        } catch (Exception e) {
            log.error("Erreur lors de la conversion en JSON de l'événement de confirmation de réinitialisation du mot de passe : {}", e.getMessage());
        }
    }
}

package tn.platformMedical.auth_user_service.services.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tn.platformMedical.auth_user_service.services.webSocket.EventHistoryService;
import tn.platformMedical.auth_user_service.services.webSocket.NotificationHandler;

/**
 * Service Kafka Consumer qui écoute les événements.
 */


@Slf4j
@Service
@AllArgsConstructor
public class KafkaConsumerService {

    private final EventHistoryService eventHistoryService;
    private final NotificationHandler notificationHandler;

    @KafkaListener(topics = "auth-user-events", groupId = "auth-user-group")
    public void listen(String message) {
        log.info("🔹 Message Kafka reçu: {}", message);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(message);

            // Extraction des données
            String eventType = jsonNode.get("event").asText();
            String email = jsonNode.get("email").asText();
            String eventMessage = jsonNode.get("message").asText();

            // Sauvegarde dans MongoDB
            eventHistoryService.saveEvent(eventType, email, eventMessage);

            // Envoi de la notification WebSocket
            String wsMessage = "📢 Événement : " + eventType + " | Utilisateur : " + email + " | Message : " + eventMessage;
            notificationHandler.sendNotification(wsMessage);

            log.info("✅ Push notified success");

        } catch (Exception e) {
            log.error("❌ Erreur lors du parsing du message Kafka : {}", e.getMessage());
        }
    }
}

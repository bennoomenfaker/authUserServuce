package tn.platformMedical.auth_user_service.models;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Entité pour stocker l'historique des événements.
 */
@Document(collection = "event_history")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventHistory {
    @Id
    private String id;
    private String eventType;
    @Email
    private String email;
    private String message;
    private Instant timestamp;
    private boolean seen; // Champ pour indiquer si l'événement a été vu

}

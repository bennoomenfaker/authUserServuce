package tn.platformMedical.auth_user_service.services.webSocket;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.platformMedical.auth_user_service.models.EventHistory;
import tn.platformMedical.auth_user_service.repository.EventHistoryRepository;

import java.time.Instant;
import java.util.List;


/**
 * Service pour la gestion des événements historiques.
 */
@Service
@RequiredArgsConstructor
public class EventHistoryService {
    private final EventHistoryRepository eventHistoryRepository;

    public void saveEvent(String eventType, String email, String message) {
        EventHistory event = EventHistory.builder()
                .eventType(eventType)
                .email(email)
                .message(message)
                .timestamp(Instant.now())
                .seen(false) // Initialement, l'événement est marqué comme non vu
                .build();
        eventHistoryRepository.save(event);
    }

    public List<EventHistory> getUserEvents(String email) {
        return eventHistoryRepository.findByEmail(email);
    }

    public List<EventHistory> getAllEvents() {
        return eventHistoryRepository.findAll();
    }

    public void markEventsAsSeen(List<EventHistory> events) {
        events.forEach(event -> {
            if (!event.isSeen()) {
                event.setSeen(true);
                eventHistoryRepository.save(event);
            }
        });



    }
}
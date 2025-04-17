package tn.platformMedical.auth_user_service.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.platformMedical.auth_user_service.models.EventHistory;
import tn.platformMedical.auth_user_service.services.webSocket.EventHistoryService;


import java.util.List;
@RestController
@RequestMapping("/api/auth-events")
@RequiredArgsConstructor
public class AuthEventController {

    private final EventHistoryService eventHistoryService;

    @GetMapping("/{email}")
    public ResponseEntity<List<EventHistory>> getUserAuthEvents(@PathVariable String email) {
        List<EventHistory> events = eventHistoryService.getUserEvents(email);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/events")
    public ResponseEntity<List<EventHistory>> getAllEvents() {
        List<EventHistory> events = eventHistoryService.getAllEvents();
        eventHistoryService.markEventsAsSeen(events);
        return ResponseEntity.ok(events);
    }
}

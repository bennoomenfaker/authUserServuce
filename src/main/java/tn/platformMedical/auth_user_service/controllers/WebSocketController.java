package tn.platformMedical.auth_user_service.controllers;




import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @MessageMapping("/notifications") // Reçoit les messages envoyés par les clients
    @SendTo("/topic/notifications")  // Diffuse aux abonnés du topic "/topic/notifications"
    public String sendNotification(String message) {
        return message;
    }
}


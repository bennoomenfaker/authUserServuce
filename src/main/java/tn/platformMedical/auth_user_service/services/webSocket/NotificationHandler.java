package tn.platformMedical.auth_user_service.services.webSocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tn.platformMedical.auth_user_service.security.jwt.JwtUtils;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * Gestionnaire de notifications WebSocket.
 */
@Slf4j
@Component
public class NotificationHandler extends TextWebSocketHandler {

    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        log.info("🔗 Nouvelle connexion WebSocket établie.");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        log.info("🔴 Connexion WebSocket fermée : {}", status);
    }

    public void sendNotification(String message) {
        for (WebSocketSession session : sessions) {
            try {
                session.sendMessage(new TextMessage(message));
                log.info("📨 Notification envoyée : {}", message);
            } catch (IOException e) {
                log.error("❌ Erreur lors de l'envoi du message WebSocket : {}", e.getMessage());
            }
        }
    }
}

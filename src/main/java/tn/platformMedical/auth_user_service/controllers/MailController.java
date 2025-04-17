package tn.platformMedical.auth_user_service.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tn.platformMedical.auth_user_service.dto.response.ApiResponse;
import tn.platformMedical.auth_user_service.exception.InvalidTokenException;
import tn.platformMedical.auth_user_service.exception.TokenExpiredException;
import tn.platformMedical.auth_user_service.services.mail.MailService;

@RestController
@RequestMapping("/api/mail") // Endpoint général pour l'authentification
@AllArgsConstructor
public class MailController {
    private final MailService mailService;

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        try {
            mailService.requestPasswordReset(email);
            return ResponseEntity.ok("Password reset email sent successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error sending password reset email: " + e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        try {
            mailService.resetPassword(token, newPassword);
            ApiResponse response = new ApiResponse("success", "Mot de passe réinitialisé avec succès");
            return ResponseEntity.ok(response);
        } catch (InvalidTokenException | TokenExpiredException e) {
            ApiResponse errorResponse = new ApiResponse("error", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        }
    }
}


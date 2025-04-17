package tn.platformMedical.auth_user_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetConfirmation {
    private String email;
    private String firstname;
    private String lastname;
    private String message;
}
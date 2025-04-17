package tn.platformMedical.auth_user_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PasswordResetRequest {
    private String token;
    private LocalDateTime expirationDate;
    private String email;
    private String firstname;
    private String lastname;
}

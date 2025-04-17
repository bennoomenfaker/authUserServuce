package tn.platformMedical.auth_user_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequest {


	@NotBlank(message = "Email is mandatory")
	@Email(message = "Email must be valid")
	private String email;

	@NotBlank(message = "Password is mandatory")
	private String password;
}

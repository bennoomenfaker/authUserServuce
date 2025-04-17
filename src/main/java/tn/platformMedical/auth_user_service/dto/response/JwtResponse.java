package tn.platformMedical.auth_user_service.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.platformMedical.auth_user_service.models.Role;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class JwtResponse {
	private String token;
	private String type = "Bearer";
	private String id;
	private String email;
	private Role role;


}

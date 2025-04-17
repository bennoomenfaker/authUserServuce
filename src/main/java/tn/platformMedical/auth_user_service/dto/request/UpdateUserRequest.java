package tn.platformMedical.auth_user_service.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import tn.platformMedical.auth_user_service.models.Role;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserRequest {

    @NotBlank(message = "First name is mandatory")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @NotBlank(message = "Telephone is mandatory")
    @Size(min = 8, max = 15, message = "Telephone must be between 8 and 15 characters")
    private String telephone;

    private Role role;

    private String password;
    private String email;

    private String serviceId;
    private String hospitalId;


}

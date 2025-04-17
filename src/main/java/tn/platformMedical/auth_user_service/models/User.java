package tn.platformMedical.auth_user_service.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private String id;

    @NotBlank(message = "First name is mandatory")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    @Indexed(unique = true)
    private String email;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "Telephone is mandatory")
    @Size(min = 8, max = 15, message = "Telephone must be between 8 and 15 characters")
    private String telephone;

    private String resetPasswordToken;
    private LocalDateTime resetPasswordTokenExpired;

    private Role role;

    /**
     * Ajout d'un champ pour stocker l'hôpital de l'utilisateur.
     * Ce champ est **null** si l'utilisateur fait partie du Ministère de la Santé.
     */
    private String hospitalId;

    /**
     * Ajout d'un champ pour stocker le service de l'utilisateur.
     * Ce champ est **null** si l'utilisateur fait partie du Ministère de la Santé ou d'un autre rôle qui n'est pas assigné à un service spécifique.
     */
    private String serviceId;

    private boolean activated = true;




    // Differentiating User Roles
    public boolean isMinistryAdmin() {
        return  "ROLE_MINISTRY_ADMIN".equals(this.role.getName());
    }

    public boolean isHospitalAdmin() {
        return  "ROLE_HOSPITAL_ADMIN".equals(this.role.getName());
    }

    public boolean isServiceSupervisor() {
        return "ROLE_SERVICE_SUPERVISOR".equals(this.role.getName());
    }

    public boolean isMaintenanceEngineer() {
        return "ROLE_MAINTENANCE_ENGINEER".equals(this.role.getName());
    }

    public boolean isMaintenanceCompanyStaff() {
        return "ROLE_MAINTENANCE_COMPANY".equals(this.role.getName());
    }

    public User(String firstName, String lastName, String email, String password, String telephone, String hospitalId, String serviceId, Role role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.telephone = telephone;
        this.hospitalId = hospitalId;
        this.serviceId = serviceId;
        this.role = role;
        this.activated = true;
    }
}

package tn.platformMedical.auth_user_service.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "roles")
public class Role {

  @Id
  private String id;

  @NotBlank(message = "Role name is mandatory")
  @Size(min = 3, max = 50, message = "Role name must be between 3 and 50 characters")
  private String name;
}

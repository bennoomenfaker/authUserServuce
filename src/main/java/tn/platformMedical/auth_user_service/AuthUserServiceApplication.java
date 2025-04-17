package tn.platformMedical.auth_user_service;

import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import tn.platformMedical.auth_user_service.dto.request.SignupRequest;
import tn.platformMedical.auth_user_service.models.Role;
import tn.platformMedical.auth_user_service.repository.RoleRepository;
import tn.platformMedical.auth_user_service.repository.UserRepository;
import tn.platformMedical.auth_user_service.services.AuthService;
import tn.platformMedical.auth_user_service.services.RoleService;

@SpringBootApplication
@EnableDiscoveryClient
@AllArgsConstructor
public class AuthUserServiceApplication {

	private final RoleService roleService;
	private final AuthService authService;
	private final RoleRepository roleRepository;
	private final UserRepository userRepository;

	public static void main(String[] args) {
		SpringApplication.run(AuthUserServiceApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner() {
		return args -> {
			// Vérification et création des rôles
			createRoleIfNotExists("ROLE_MINISTRY_ADMIN");
			createRoleIfNotExists("ROLE_HOSPITAL_ADMIN");
			createRoleIfNotExists("ROLE_SERVICE_SUPERVISOR");
			createRoleIfNotExists("ROLE_MAINTENANCE_ENGINEER");
			createRoleIfNotExists("ROLE_MAINTENANCE_COMPANY");

			// Création des utilisateurs par défaut
			createDefaultUsers();
		};
	}

	private void createRoleIfNotExists(String roleName) {
		if (!roleRepository.existsByName(roleName)) {
			Role role = new Role(null, roleName);
			roleService.createRole(role);
		}
	}

	private void createUserIfNotExists(SignupRequest request) {
		if (!userRepository.existsByEmail(request.getEmail())) {
			authService.registerUser(request);
		}
	}

	private void createDefaultUsers() {


		createUserIfNotExists(new SignupRequest("Ministry Admin", "Health", "fakernoomen@gmail.com", "admin_password", "111111111", null, null, "ROLE_MINISTRY_ADMIN"));
		createUserIfNotExists(new SignupRequest("Hospital Admin", "Hospital", "fakerbennoomen@gmail.com", "admin_password", "333333333", "hospitalId1", null, "ROLE_HOSPITAL_ADMIN"));
		createUserIfNotExists(new SignupRequest("Service Supervisor", "Hospital", "service.supervisor@example.com", "supervisor_password", "444444444", "hospitalId1", "serviceId1", "ROLE_SERVICE_SUPERVISOR"));
		createUserIfNotExists(new SignupRequest("Maintenance Engineer", "Hospital", "maintenance.engineer@example.com", "engineer_password", "555555555", "hospitalId1", "serviceId2", "ROLE_MAINTENANCE_ENGINEER"));
		createUserIfNotExists(new SignupRequest("Maintenance Company", "Company", "maintenance.company@example.com", "company_password", "666666666", null, null, "ROLE_MAINTENANCE_COMPANY"));

	}
}
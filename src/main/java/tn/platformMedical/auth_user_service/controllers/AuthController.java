package tn.platformMedical.auth_user_service.controllers;

import jakarta.validation.Valid;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import tn.platformMedical.auth_user_service.dto.request.LoginRequest;
import tn.platformMedical.auth_user_service.dto.request.SignupRequest;
import tn.platformMedical.auth_user_service.dto.response.JwtResponse;
import tn.platformMedical.auth_user_service.dto.response.MessageResponse;
import tn.platformMedical.auth_user_service.models.User;
import tn.platformMedical.auth_user_service.repository.RoleRepository;
import tn.platformMedical.auth_user_service.repository.UserRepository;
import tn.platformMedical.auth_user_service.security.jwt.JwtUtils;
import tn.platformMedical.auth_user_service.security.services.UserDetailsImpl;
import tn.platformMedical.auth_user_service.services.AuthService;
import tn.platformMedical.auth_user_service.services.kafka.KafkaProducerService;

import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	JwtUtils jwtUtils;

	@Autowired
	AuthService authService;


    @Autowired
    KafkaProducerService kafkaProducerService;

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
		ResponseEntity<?> response = authService.authenticateUser(loginRequest);

		return ResponseEntity.status(HttpStatus.CREATED).body(response.getBody());
	}



	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {

		ResponseEntity<?> response = authService.registerUser(signUpRequest);


		kafkaProducerService.sendMessage("auth-user-events", "USER_SIGNUP",
				signUpRequest.getEmail(), "Un nouvel utilisateur a été créé"+ signUpRequest.getFirstName()+signUpRequest.getLastName()+signUpRequest.getEmail());

		return response;
	}


	@GetMapping("/profile")
	public ResponseEntity<?> getProfile(@RequestHeader(value = "Authorization", required = false) String authHeader) {
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token invalide");
		}
		String token = authHeader.substring(7); // Enlever "Bearer "
		return authService.getProfile(token);
	}



	/**
	 *  exist user by email
	 */
	@GetMapping("/exists")
	public ResponseEntity<Boolean> checkUserExistsByEmail(@RequestParam String email) {
		Boolean existed = authService.existUserByEmail(email);

			return ResponseEntity.ok(existed);

	}
}

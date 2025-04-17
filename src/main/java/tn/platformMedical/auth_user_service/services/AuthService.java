package tn.platformMedical.auth_user_service.services;

import org.springframework.http.ResponseEntity;
import tn.platformMedical.auth_user_service.dto.request.LoginRequest;
import tn.platformMedical.auth_user_service.dto.request.SignupRequest;
import tn.platformMedical.auth_user_service.dto.response.JwtResponse;
import tn.platformMedical.auth_user_service.models.User;

import java.util.Optional;

public interface AuthService {
    ResponseEntity<?> registerUser(SignupRequest signUpRequest);
    ResponseEntity<JwtResponse> authenticateUser(LoginRequest loginRequest);
    ResponseEntity<?> getProfile(String token);
    Boolean existUserByEmail(String email);


}

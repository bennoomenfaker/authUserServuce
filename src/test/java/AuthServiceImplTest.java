import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import tn.platformMedical.auth_user_service.dto.request.LoginRequest;
import tn.platformMedical.auth_user_service.dto.response.JwtResponse;
import tn.platformMedical.auth_user_service.models.Role;
import tn.platformMedical.auth_user_service.repository.RoleRepository;
import tn.platformMedical.auth_user_service.repository.UserRepository;
import tn.platformMedical.auth_user_service.security.jwt.JwtUtils;
import tn.platformMedical.auth_user_service.security.services.UserDetailsImpl;
import tn.platformMedical.auth_user_service.services.AuthServiceImpl;

import java.util.List;


@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImplTest.class);

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void testAuthenticateUser_Success() {
        // Arrange
        String email = "fakerbennoomen@gmail.com";
        String password = "17092001";
        String jwtToken = "fake-jwt-token";

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        Role role = new Role("", "ROLE_HOSPITAL_ADMIN");
        GrantedAuthority authority = new SimpleGrantedAuthority(role.getName());
        UserDetailsImpl userDetails = new UserDetailsImpl("user-id", email, password, List.of(authority), role);

        Authentication authenticationMock = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(authenticationMock);
        when(authenticationMock.getPrincipal()).thenReturn(userDetails);
        when(jwtUtils.generateJwtToken(authenticationMock)).thenReturn(jwtToken);

        // Act
        ResponseEntity<JwtResponse> response = authService.authenticateUser(loginRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertEquals(jwtToken, jwtResponse.getToken());
        assertEquals(email, jwtResponse.getEmail());
        assertEquals("Bearer", jwtResponse.getType());
        assertEquals("ROLE_HOSPITAL_ADMIN", jwtResponse.getRole().getName());

        //  Logs dans la console
        log.info(" Test réussi : Authentification avec succès !");
        log.info(" JWT Token : {}", jwtResponse.getToken());
        log.info(" Email : {}", jwtResponse.getEmail());
        log.info(" Rôle : {}", jwtResponse.getRole().getName());
    }
}
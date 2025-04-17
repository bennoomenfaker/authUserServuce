package tn.platformMedical.auth_user_service.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tn.platformMedical.auth_user_service.security.jwt.AuthEntryPointJwt;
import tn.platformMedical.auth_user_service.security.jwt.AuthTokenFilter;
import tn.platformMedical.auth_user_service.security.services.UserDetailsServiceImpl;

/**
 * Configuration de la sécurité web de l'application.
 * Cette classe configure la sécurité web en utilisant Spring Security, en définissant les règles d'authentification,
 * les filtres de sécurité et les encodeurs de mot de passe.
 */
@Configuration
@EnableMethodSecurity

public class WebSecurityConfig {
  @Autowired
  UserDetailsServiceImpl userDetailsService;

  @Autowired
  private AuthEntryPointJwt unauthorizedHandler; // Gestionnaire des erreurs d'authentification
  /**
   * Crée un bean pour le filtre d'authentification JWT.
   * Ce filtre est utilisé pour valider les tokens JWT dans les requêtes entrantes.
   *
   * @return un nouvel objet AuthTokenFilter.
   */
  @Bean
  public AuthTokenFilter authenticationJwtTokenFilter() {
    return new AuthTokenFilter();
  }


  /**
   * Crée un bean pour le fournisseur d'authentification.
   * Ce fournisseur utilise le service de détails utilisateur et l'encodeur de mot de passe pour l'authentification.
   *
   * @return un nouvel objet DaoAuthenticationProvider configuré.
   */
  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder());

    return authProvider;
  }
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }



  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
    return authConfig.getAuthenticationManager();
  }


  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
            .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/auth/**").permitAll()
                    .requestMatchers("/api/mail/**").permitAll()
                    .requestMatchers("/api/users/**").permitAll()
                    .requestMatchers("/swagger-ui/**").permitAll()
                    .requestMatchers("/v3/api-docs/**").permitAll()
                    .requestMatchers("/actuator/**").permitAll()
                    .requestMatchers("/ws/**").permitAll() // Autoriser WebSocket
                    .requestMatchers("/notifications/**").permitAll() //  Autoriser WebSocket
                    .anyRequest().authenticated()
            );

    http.authenticationProvider(authenticationProvider());
    http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
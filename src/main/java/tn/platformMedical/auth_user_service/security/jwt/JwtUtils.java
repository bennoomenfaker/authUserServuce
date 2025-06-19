package tn.platformMedical.auth_user_service.security.jwt;

import java.security.Key;
import java.util.Date;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import tn.platformMedical.auth_user_service.security.services.UserDetailsImpl;

/**
 * Utilitaire pour la gestion des tokens JWT (JSON Web Tokens).
 * Cette classe fournit des méthodes pour générer, valider et extraire des informations des tokens JWT.
 */
@Component
public class JwtUtils {
  private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

  @Value("${jwt.secret}")
  private String jwtSecret; // Clé secrète pour signer les JWT, injectée depuis les propriétés de l'application

  @Value("${jwt.expiration-ms}")
  private long jwtExpirationMs; // Durée de validité du JWT en millisecondes, injectée depuis les propriétés de l'application

  private Key key;

  @PostConstruct
  public void init() {
    this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
  }

  private Key getSigningKey() {
    return key;
  }

  /**
   * Génère un token JWT à partir de l'objet Authentication.
   *
   * @param authentication Objet Authentication contenant les détails de l'utilisateur authentifié.
   * @return Le token JWT généré sous forme de chaîne de caractères.
   */
  public String generateJwtToken(Authentication authentication) {
    UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
    return Jwts.builder()
            .setSubject(userPrincipal.getUsername())
            .claim("role", userPrincipal.getRole().getName())
            .setIssuedAt(new Date())
            .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256) // <-- Corrigé ici
            .compact();
  }

  /**
   * Extrait le nom d'utilisateur à partir du token JWT.
   *
   * @param token Le token JWT à partir duquel extraire le nom d'utilisateur.
   * @return Le nom d'utilisateur extrait du token JWT.
   */
  public String getUserNameFromJwtToken(String token) {
    return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
  }

  /**
   * Valide le token JWT en vérifiant sa signature et sa validité.
   *
   * @param authToken Le token JWT à valider.
   * @return True si le token est valide, sinon False.
   */
  public boolean validateJwtToken(String authToken) {
    try {
      System.out.println("Validation du token JWT");
      Jwts.parserBuilder()
              .setSigningKey(getSigningKey())
              .build()
              .parse(authToken);
      System.out.println("Token JWT valide");
      return true;
    } catch (ExpiredJwtException e) {
      logger.error("JWT token is expired: {}", e.getMessage());
      System.out.println("Token expiré détecté dans JwtUtils");
      throw e;
    } catch (MalformedJwtException e) {
      logger.error("Invalid JWT token: {}", e.getMessage());
    } catch (UnsupportedJwtException e) {
      logger.error("JWT token is unsupported: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      logger.error("JWT claims string is empty: {}", e.getMessage());
    }
    return false;
  }

  /**
   * Extrait le rôle utilisateur à partir du token JWT.
   *
   * @param token Le token JWT à partir duquel extraire le rôle utilisateur.
   * @return Le rôle utilisateur extrait du token JWT.
   */
  public String getUserRoleFromJwtToken(String token) {
    Claims claims = Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    return claims.get("role", String.class);
  }
}

package tn.platformMedical.auth_user_service.security.services;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import tn.platformMedical.auth_user_service.models.Role;
import tn.platformMedical.auth_user_service.models.User;

/**
 * Implémentation de l'interface UserDetails de Spring Security.
 * Cette classe représente un détail utilisateur personnalisé utilisé par Spring Security pour l'authentification et l'autorisation.
 */
/**a classe UserDetailsImpl implémente l'interface UserDetails de Spring Security pour fournir les détails de l'utilisateur
 à Spring Security lors de l'authentification et de l'autorisation.*/

/**Cette classe est ensuite utilisée dans votre implémentation de UserDetailsService (UserDetailsServiceImpl**/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsImpl implements UserDetails {
	private static final long serialVersionUID = 1L;

	@Getter
    private String id;
	private String email;

	@JsonIgnore
	private String password;//@JsonIgnore pour éviter de l'exposer dans les réponses JSON


	private Collection<? extends GrantedAuthority> authorities;
	private Role role;
	/**
	 * Construit une instance de UserDetailsImpl à partir d'une entité User.
	 * Convertit les rôles de l'utilisateur en une collection d'objets GrantedAuthority.
	 *
	 * @param user L'entité utilisateur à partir de laquelle construire l'instance de UserDetailsImpl.
	 * @return Une instance de UserDetailsImpl avec les détails et les autorités de l'utilisateur.
	 */

	/****/



	public static UserDetailsImpl build(User user) {
		GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().getName());
		return new UserDetailsImpl(
				user.getId(),
				user.getEmail(),
				user.getPassword(),
				Collections.singletonList(authority),
				user.getRole()
		);
	}
	// Récupère le rôle unique de l'utilisateur à partir de  authorities
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}



	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return email;  // Utilisation de l'email comme nom d'utilisateur
	}

	@Override
	public boolean isAccountNonExpired() {
		return true; // Indique que le compte n'est pas expiré
	}

	@Override
	public boolean isAccountNonLocked() {
		return true; // Indique que le compte n'est pas verrouillé
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true; // Indique que les informations d'identification (mot de passe) ne sont pas expirées
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true; // Vérifie si l'objet courant et l'objet fourni sont la même instance
		if (o == null || getClass() != o.getClass()) return false; // Vérifie si l'objet fourni est de la même classe
		UserDetailsImpl user = (UserDetailsImpl) o; // Cast l'objet en UserDetailsImpl
		return Objects.equals(id, user.id); // Compare en fonction de l'ID utilisateur
	}
	// Génère le code de hachage en fonction de l'ID utilisateur
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}


}
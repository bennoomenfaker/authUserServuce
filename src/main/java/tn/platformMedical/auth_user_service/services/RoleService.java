package tn.platformMedical.auth_user_service.services;

import tn.platformMedical.auth_user_service.models.Role;

import java.util.List;
import java.util.Optional;

public interface RoleService {

    Role createRole(Role role);

    Optional<Role> getRoleById(String id);

    List<Role> getAllRoles();

    Role updateRole(Role role);

    void deleteRole(String id);
}

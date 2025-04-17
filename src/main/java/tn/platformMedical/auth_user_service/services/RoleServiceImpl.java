package tn.platformMedical.auth_user_service.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.platformMedical.auth_user_service.models.Role;
import tn.platformMedical.auth_user_service.repository.RoleRepository;

import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Role createRole(Role role) {
        role.setName(role.getName().toUpperCase());
        return roleRepository.save(role);
    }

    @Override
    public Optional<Role> getRoleById(String id) {
        return roleRepository.findById(id);
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Role updateRole(Role role) {
        role.setName(role.getName().toUpperCase());
        return roleRepository.save(role);
    }

    @Override
    public void deleteRole(String id) {
        roleRepository.deleteById(id);
    }
}

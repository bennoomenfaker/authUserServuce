package tn.platformMedical.auth_user_service.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.platformMedical.auth_user_service.dto.response.MessageResponse;
import tn.platformMedical.auth_user_service.models.Role;
import tn.platformMedical.auth_user_service.services.RoleService;
import tn.platformMedical.auth_user_service.services.kafka.KafkaProducerService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    KafkaProducerService kafkaProducerService;

    @PreAuthorize("hasRole('ROLE_MINISTRY_ADMIN')")
    @PostMapping
    public ResponseEntity<?> createRole(@RequestBody Role role) {
        if (roleService.getAllRoles().stream().anyMatch(r -> r.getName().equalsIgnoreCase(role.getName()))) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Role already exists!"));
        }
        Role createdRole = roleService.createRole(role);

        kafkaProducerService.sendMessage("auth-user-events", "ROLE_CREATED",
                role.getId(), "Le rôle " + role.getName() + " a été ajouté.");
        return ResponseEntity.ok(createdRole);
    }

    @PreAuthorize("hasRole('ROLE_MINISTRY_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Role> getRoleById(@PathVariable String id) {
        Optional<Role> role = roleService.getRoleById(id);
        return role.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ROLE_MINISTRY_ADMIN') or hasRole('ROLE_HOSPITAL_ADMIN')")
    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    @PreAuthorize("hasRole('ROLE_MS_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRole(@PathVariable String id, @RequestBody Role role) {
        role.setId(id);
        if (roleService.getAllRoles().stream().anyMatch(r -> r.getName().equalsIgnoreCase(role.getName()) && !r.getId().equals(id))) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Role name conflict!"));
        }
        Role updatedRole = roleService.updateRole(role);
        return ResponseEntity.ok(updatedRole);
    }
    @PreAuthorize("hasRole('ROLE_MINISTRY_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable String id) {
        Optional<Role> roleOptional = roleService.getRoleById(id);
        if (!roleOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        roleService.deleteRole(id);
        kafkaProducerService.sendMessage("auth-user-events", "ROLE_DELETED",
                id, "Le rôle " + roleOptional.get().getName() + " a été supprimé.");

        return ResponseEntity.noContent().build();
    }

}

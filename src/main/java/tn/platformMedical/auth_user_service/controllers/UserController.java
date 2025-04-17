package tn.platformMedical.auth_user_service.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.platformMedical.auth_user_service.dto.request.UpdateHospitalServiceRequest;
import tn.platformMedical.auth_user_service.dto.request.UpdateUserRequest;
import tn.platformMedical.auth_user_service.models.User;
import tn.platformMedical.auth_user_service.security.jwt.JwtUtils;
import tn.platformMedical.auth_user_service.services.UserService;
import tn.platformMedical.auth_user_service.services.kafka.KafkaProducerService;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    private final UserService userService;
    private final JwtUtils jwtUtils;
    @Autowired
    KafkaProducerService kafkaProducerService;
    /**
     * 🔹 Récupérer un utilisateur par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Optional<User>> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }


    /**
     * 🔹 Récupérer tous les utilisateurs (seulement pour MS_ADMIN)
     */
    @PreAuthorize("hasRole('ROLE_MINISTRY_ADMIN')")
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * 🔹 Modifier un utilisateur (seul HOSPITAL_ADMIN ou MS_ADMIN peut changer le rôle)
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable String id,
            @Valid @RequestBody UpdateUserRequest updateUserRequest,
            @RequestHeader("Authorization") String token) {

        // Vérifier si le token est null ou ne commence pas par "Bearer"
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Extraire le token en supprimant les 7 premiers caractères ("Bearer ")
        String cleanToken = token.substring(7);

        // Afficher le token nettoyé
        System.out.println(cleanToken);

        // Récupérer le rôle de l'utilisateur à partir du token JWT
        String requesterRole = jwtUtils.getUserRoleFromJwtToken(cleanToken);
        System.out.println(requesterRole);

        // Mettre à jour l'utilisateur
        User updatedUser = userService.updateUser(id, updateUserRequest, requesterRole);

        // Envoyer un message Kafka
        kafkaProducerService.sendMessage("auth-user-events", "USER_UPDATED",
                id, "L'utilisateur avec ID " + id + " a été mis à jour.");

        // Retourner la réponse
        return ResponseEntity.ok(updatedUser);
    }
    /**
     * 🔹 Supprimer un utilisateur (seulement MS_ADMIN)
     */
    @PreAuthorize("hasRole('ROLE_HOSPITAL_ADMIN') or hasRole('ROLE_MINISTRY_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        kafkaProducerService.sendMessage("auth-user-events", "USER_DELETED",
                id, "L'utilisateur avec ID " + id + " a été supprimé.");
        return ResponseEntity.ok("User deleted successfully");
    }

    /**
     * 🔹 activer un utilisateur (seulement MS_ADMIN)
     */
    @PreAuthorize("hasRole('ROLE_HOSPITAL_ADMIN') or hasRole('ROLE_MINISTRY_ADMIN')")
    @PostMapping("/{id}")
    public ResponseEntity<?> activateUser(@PathVariable String id) {
        userService.activateUser(id);
        kafkaProducerService.sendMessage("auth-user-events", "USER_DELETED",
                id, "L'utilisateur avec ID " + id + " a été supprimé.");
        return ResponseEntity.ok("User deleted successfully");
    }

    /**
     * 🔹 Récupérer les utilisateurs du ministère de la santé
     */
    @PreAuthorize("hasRole('ROLE_MINISTRY_ADMIN')")
    @GetMapping("/ministere")
    public ResponseEntity<List<User>> getUsersDeMinistere() {
        return ResponseEntity.ok(userService.getUsersDuMinistere());
    }

    /**
     * 🔹 Récupérer les utilisateurs d'un hôpital spécifique
     */
    @GetMapping("/hospital/{hospitalId}")
    public ResponseEntity<List<User>> getUsersByHospitalId(@PathVariable String hospitalId) {
        return ResponseEntity.ok(userService.getUsersByHospitalId(hospitalId));
    }

    /**
     * 🔹 Récupérer les administrateurs des hôpitaux
     */

    @PreAuthorize("hasRole('ROLE_MINISTRY_ADMIN')")
    @GetMapping("/admins-hopitaux")
    public ResponseEntity<List<User>> getAdminsDesHopitaux() {
        return ResponseEntity.ok(userService.getAdminsDesHopitaux());
    }

    /**
     * 🔹 Récupérer les utlisateurs par service
     */

    @PreAuthorize("hasRole('ROLE_HOSPITAL_ADMIN') or hasRole('ROLE_MINISTRY_ADMIN')")
    @GetMapping("/hospital/service/{serviceId}")
    public ResponseEntity<List<User>> findUserByServiceId(@PathVariable String serviceId) {
        return ResponseEntity.ok(userService.getUsersByServiceId(serviceId));
    }

    /**
     * 🔹 Récupérer les utilisateurs d'un service spécifique dans un hôpital spécifique
     */
    @GetMapping("/hospital/{hospitalId}/service/{serviceId}")
    @PreAuthorize("hasRole('ROLE_HOSPITAL_ADMIN') or hasRole('ROLE_MINISTRY_ADMIN')")
    public ResponseEntity<List<User>> getUsersByServiceIdAndHospitalId(
            @PathVariable String hospitalId,
            @PathVariable String serviceId) {
        return ResponseEntity.ok(userService.getUsersByServiceIdAndHospitalId(serviceId, hospitalId));
    }


    @PutMapping("/{id}/update-hospital-service")
    public ResponseEntity<String> updateUserHospitalAndService(
            @PathVariable String id,
            @RequestBody UpdateHospitalServiceRequest request) {

        userService.updateUserHospitalAndService(id, request.getHospitalId(), request.getServiceId());

        return ResponseEntity.ok("User hospital and service updated successfully.");
    }

    @GetMapping("/hospital/{hospitalId}/roles")
    public ResponseEntity<List<User>> getUsersByHospitalAndRoles(
            @PathVariable String hospitalId,
            @RequestParam List<String> roles) {
        System.out.println(roles);
        return ResponseEntity.ok(userService.getUsersByHospitalAndRoles(hospitalId, roles));
    }
    // Récupérer l'admin d'un hôpital à partir de l'hospitalId
    @GetMapping("/hospital/{hospitalId}/admin")
    public ResponseEntity<User> getAdminByHospitalId(
            @RequestHeader("Authorization") String token,
            @PathVariable String hospitalId) {

        // Appeler le service pour obtenir l'admin de l'hôpital
        User admin = userService.getAdminByHospitalId(hospitalId);
        return ResponseEntity.ok(admin);
    }

    @GetMapping("/supervisors/{serviceId}")
    public ResponseEntity<List<User>> getServiceSupervisors(@PathVariable String serviceId) {
        List<User> supervisors = userService.getServiceSupervisors(serviceId);
        return ResponseEntity.ok(supervisors);
    }



}

package tn.platformMedical.auth_user_service.services;

import lombok.AllArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tn.platformMedical.auth_user_service.dto.request.UpdateUserRequest;
import tn.platformMedical.auth_user_service.models.User;
import tn.platformMedical.auth_user_service.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public User updateUser(String id, UpdateUserRequest updateUserRequest, String requesterRole) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        user.setFirstName(updateUserRequest.getFirstName());
        user.setLastName(updateUserRequest.getLastName());
        user.setTelephone(updateUserRequest.getTelephone());
        user.setServiceId(updateUserRequest.getServiceId());
        user.setHospitalId(updateUserRequest.getHospitalId());
        user.setEmail(updateUserRequest.getEmail());

        //  Si un mot de passe est fourni, on l'encode, sinon on garde l'ancien
        if (updateUserRequest.getPassword() != null && !updateUserRequest.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updateUserRequest.getPassword()));
        }

        // Seuls les utilisateurs avec le rôle MS_ADMIN ou HOSPITAL_ADMIN peuvent modifier le rôle
        if ("ROLE_MINISTRY_ADMIN".equals(requesterRole) || "ROLE_HOSPITAL_ADMIN".equals(requesterRole)) {
            user.setRole(updateUserRequest.getRole());
        }

        return userRepository.save(user);
    }

    @Override
    public void deleteUser(String id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (!optionalUser.isPresent()) {
            throw new ResourceNotFoundException("User not found with ID: " + id);
        }

        User user = optionalUser.get();
        user.setActivated(false);
        userRepository.save(user);
    }




    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void activateUser(String id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (!optionalUser.isPresent()) {
            throw new ResourceNotFoundException("User not found with ID: " + id);
        }

        User user = optionalUser.get();
        user.setActivated(true);
        userRepository.save(user);

    }

    @Override
    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> getUsersDuMinistere() {
        return userRepository.findByRole_Name("ROLE_MS_STAFF");
    }

    @Override
    public List<User> getUsersByHospitalId(String hospitalId) {
        return userRepository.findByHospitalId(hospitalId);
    }

    @Override
    public List<User> getAdminsDesHopitaux() {
        return userRepository.findByRole_Name("ROLE_HOSPITAL_ADMIN");
    }

   @Override
    public List<User> getUsersByServiceId(String serviceId) {
        return userRepository.findByServiceId(serviceId);
    }

   @Override
    public List<User> getUsersByServiceIdAndHospitalId(String serviceId, String hospitalId) {
        return userRepository.findByServiceIdAndHospitalId(serviceId, hospitalId);    }

    @Override
    public void updateUserHospitalAndService(String userId, String hospitalId, String serviceId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        user.setHospitalId(hospitalId);
        user.setServiceId(serviceId);

        userRepository.save(user);
    }

    @Override
    public User getAdminByHospitalId(String hospitalId) {
        // Chercher un utilisateur avec le rôle "ROLE_HOSPITAL_ADMIN" pour cet hôpital
        User admin = userRepository.findByHospitalIdAndRole_Name(hospitalId, "ROLE_HOSPITAL_ADMIN")
                .orElseThrow(() -> new ResourceNotFoundException("Aucun admin trouvé pour l'hôpital avec l'ID: " + hospitalId));

        // Retourner un nouvel objet User avec les données trouvées
        return   admin;
    }

    @Override
    public List<User> getUsersByHospitalAndRoles(String hospitalId, List<String> roleNames) {
        return userRepository.findUsersByHospitalOrMinistryAdmin(hospitalId, roleNames);
    }

    @Override
    public List<User> getServiceSupervisors(String serviceId) {
        return userRepository.findByServiceIdAndRole_Name(serviceId, "ROLE_SERVICE_SUPERVISOR");
    }




}

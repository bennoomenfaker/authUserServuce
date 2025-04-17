package tn.platformMedical.auth_user_service.services;

import tn.platformMedical.auth_user_service.dto.request.UpdateUserRequest;
import tn.platformMedical.auth_user_service.models.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User updateUser(String id, UpdateUserRequest updateUserRequest, String requesterRole);
    void deleteUser(String id);
    List<User> getAllUsers();
    void activateUser(String id);
    Optional<User> getUserById(String id);
    List<User> getUsersDuMinistere();
    List<User> getUsersByHospitalId(String hospitalId);
    List<User> getAdminsDesHopitaux();
    List<User> getUsersByServiceId(String serviceId) ;
    List<User> getUsersByServiceIdAndHospitalId(String serviceId, String hospitalId);
    void updateUserHospitalAndService(String userId, String hospitalId, String serviceId);

     User getAdminByHospitalId(String hospitalId);
    List<User> getUsersByHospitalAndRoles(String hospitalId, List<String> roles);
    List<User> getServiceSupervisors(String serviceId);
}

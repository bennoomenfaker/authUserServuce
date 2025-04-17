package tn.platformMedical.auth_user_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import tn.platformMedical.auth_user_service.models.User;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
  Optional<User> findByEmail(String email);

  List<User> findByHospitalId(String hospitalId);

  List<User> findByRole_Name(String roleName);
  Boolean existsByEmail(String email);

  Optional<User> findByResetPasswordToken(String token);

  List<User> findByServiceIdAndHospitalId(String serviceId, String hospitalId);

  List<User> findByServiceId(String serviceId);
  @Query("{'$or': [ {'hospitalId': ?0, 'role.name': {'$in': ?1}}, {'role.name': 'ROLE_MINISTRY_ADMIN'} ]}")
  List<User> findUsersByHospitalOrMinistryAdmin(String hospitalId, List<String> roleNames);

  Optional<User> findByHospitalIdAndRole_Name(String hospitalId, String roleName);
  List<User> findByServiceIdAndRole_Name(String serviceId, String roleName);

}

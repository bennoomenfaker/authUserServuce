package tn.platformMedical.auth_user_service.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tn.platformMedical.auth_user_service.models.EventHistory;

import java.util.List;

@Repository
public interface EventHistoryRepository extends MongoRepository<EventHistory, String> {
    List<EventHistory> findByEmail(String email);

}

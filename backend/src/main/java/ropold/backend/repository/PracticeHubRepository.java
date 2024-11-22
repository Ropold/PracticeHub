package ropold.backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ropold.backend.model.RoomModel;

@Repository
public interface PracticeHubRepository extends MongoRepository<RoomModel, String> {
}

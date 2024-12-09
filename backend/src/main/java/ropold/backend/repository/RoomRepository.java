package ropold.backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ropold.backend.model.RoomModel;

@Repository
public interface RoomRepository extends MongoRepository<RoomModel, String> {
    // add custom search for active rooms here
}

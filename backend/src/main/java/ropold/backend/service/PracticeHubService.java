package ropold.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ropold.backend.exception.RoomNotFoundException;
import ropold.backend.model.RoomModel;
import ropold.backend.repository.PracticeHubRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PracticeHubService {

    private final IdService idService;
    private final PracticeHubRepository practiceHubRepository;

    public List<RoomModel> getAllRooms() {
        return practiceHubRepository.findAll();
    }

    public RoomModel getRoomById(String id) {
        return practiceHubRepository
                .findById(id)
                .orElseThrow(()-> new RoomNotFoundException("No Room found with id: " + id));
    }

    public RoomModel addRoom(RoomModel roomModel) {
        RoomModel newRoomModel = new RoomModel(
                idService.generateRandomId(),
                roomModel.name(),
                roomModel.address(),
                roomModel.category(),
                roomModel.description(),
                roomModel.wishlistStatus()
        );
        return practiceHubRepository.save(newRoomModel);
    }

    public RoomModel updateRoomWithPut(String id, RoomModel roomModel) {
        if(practiceHubRepository.existsById(id)) {
            RoomModel updatedRoomModel = new RoomModel(
                    id,
                    roomModel.name(),
                    roomModel.address(),
                    roomModel.category(),
                    roomModel.description(),
                    roomModel.wishlistStatus()
            );
            return practiceHubRepository.save(updatedRoomModel);
        } else {
            throw new RoomNotFoundException("No Room found to update with id: " + id);
        }
    }

    public void deleteRoom(String id) {
        if (!practiceHubRepository.existsById(id)) {
            throw new RoomNotFoundException("No Room found to delete with id: " + id);
        }
        practiceHubRepository.deleteById(id);
    }
}

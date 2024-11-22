package ropold.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ropold.backend.exception.RoomNotFoundException;
import ropold.backend.model.RoomModel;
import ropold.backend.repository.PracticeHubRepository;

import java.util.List;
import java.util.NoSuchElementException;

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
}

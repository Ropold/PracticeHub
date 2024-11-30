package ropold.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ropold.backend.exception.RoomNotFoundException;
import ropold.backend.model.RoomModel;
import ropold.backend.repository.RoomRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final IdService idService;
    private final RoomRepository roomRepository;

    public List<RoomModel> getAllRooms() {
        return roomRepository.findAll();
    }

    public RoomModel getRoomById(String id) {
        return roomRepository
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
                roomModel.wishlistStatus(),
                roomModel.imageUrl()
        );
        return roomRepository.save(newRoomModel);
    }

    public RoomModel updateRoomWithPut(String id, RoomModel roomModel) {
        if(roomRepository.existsById(id)) {
            RoomModel updatedRoomModel = new RoomModel(
                    id,
                    roomModel.name(),
                    roomModel.address(),
                    roomModel.category(),
                    roomModel.description(),
                    roomModel.wishlistStatus(),
                    roomModel.imageUrl()
            );
            return roomRepository.save(updatedRoomModel);
        } else {
            throw new RoomNotFoundException("No Room found to update with id: " + id);
        }
    }

    public void deleteRoom(String id) {
        if (!roomRepository.existsById(id)) {
            throw new RoomNotFoundException("No Room found to delete with id: " + id);
        }
        roomRepository.deleteById(id);
    }
}

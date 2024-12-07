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
    private final CloudinaryService cloudinaryService;

    public List<RoomModel> getAllRooms() {
        return roomRepository.findAll();
    }

    public List<RoomModel> getActiveRooms() {
        return roomRepository.findAll().stream()
                .filter(RoomModel::isActive)
                .toList();
    }

    public RoomModel getRoomById(String id) {
        return roomRepository
                .findById(id)
                .orElseThrow(()-> new RoomNotFoundException("No Room found with id: " + id));
    }

    public List<RoomModel> getRoomsByIds(List<String> roomIds) {
        return roomRepository.findAllById(roomIds);
    }

    public RoomModel addRoom(RoomModel roomModel) {
        RoomModel newRoomModel = new RoomModel(
                idService.generateRandomId(),
                roomModel.name(),
                roomModel.address(),
                roomModel.category(),
                roomModel.description(),
                roomModel.appUserGithubId(),
                roomModel.appUserUsername(),
                roomModel.appUserAvatarUrl(),
                roomModel.appUserGithubUrl(),
                roomModel.isActive(),
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
                    roomModel.appUserGithubId(),
                    roomModel.appUserUsername(),
                    roomModel.appUserAvatarUrl(),
                    roomModel.appUserGithubUrl(),
                    roomModel.isActive(),
                    roomModel.imageUrl()
            );
            return roomRepository.save(updatedRoomModel);
        } else {
            throw new RoomNotFoundException("No Room found to update with id: " + id);
        }
    }

    public RoomModel toggleActiveStatus(String id) {
        RoomModel room = roomRepository.findById(id)
                .orElseThrow(() -> new RoomNotFoundException("No Room found with id: " + id));

        RoomModel updatedRoomModel = new RoomModel(
                id,
                room.name(),
                room.address(),
                room.category(),
                room.description(),
                room.appUserGithubId(),
                room.appUserUsername(),
                room.appUserAvatarUrl(),
                room.appUserGithubUrl(),
                !room.isActive(),
                room.imageUrl()
        );

        return roomRepository.save(updatedRoomModel);
    }

    public void deleteRoom(String id) {
        RoomModel room = roomRepository.findById(id)
                .orElseThrow(() -> new RoomNotFoundException("Kein Raum gefunden zum Löschen mit der ID: " + id));

        if (room.imageUrl() != null) {
            cloudinaryService.deleteImage(room.imageUrl());
        }
        roomRepository.deleteById(id);
    }
}

package ropold.backend.service;

import org.junit.jupiter.api.Test;
import ropold.backend.model.Category;
import ropold.backend.model.RoomModel;
import ropold.backend.repository.RoomRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


class RoomServiceTest {
    IdService idService = mock(IdService.class);
    RoomRepository roomRepository = mock(RoomRepository.class);
    CloudinaryService cloudinaryService = mock(CloudinaryService.class);
    RoomService testRoomService = new RoomService(idService, roomRepository,cloudinaryService);

    RoomModel roomModel = new RoomModel("1", "Gürzenich Saal", "Martinstr. 29 50667 Köln",Category.ORCHESTER_HALL,"Ein traditionsreicher Saal für Konzerte und Veranstaltungen.",
            "154427648", "Ropold", "https://avatars.githubusercontent.com/u/154427648?v=4",
            "https://github.com/Ropold", true, "https://res.cloudinary.com/dzjjlydk3/image/upload/v1733477653/sqw62kggomno2bufjaoi.jpg");
    RoomModel roomModel2 = new RoomModel("2","Bandraum *gemütlich*","Kreuzstraße 17, 50670 Köln",Category.BAND_ROOM,"Ein gemütlicher, gut gedämmter Raum zum Üben und Proben.",
            "154427648","Ropold","https://avatars.githubusercontent.com/u/154427648?v=4","https://github.com/Ropold",false,"https://res.cloudinary.com/dzjjlydk3/image/upload/v1733473109/dauqufxqzou9akwyoxha.jpg");

    List<RoomModel> rooms = List.of(roomModel, roomModel2);

    @Test
    void getAllRooms() {
        // Given
        when(roomRepository.findAll()).thenReturn(rooms);

        // When
        List<RoomModel> expected = testRoomService.getAllRooms();

        // Then
        assertEquals(expected, rooms);
    }
    @Test
    void getRoomById() {
        // Given
        when(roomRepository.findById("1")).thenReturn(Optional.of(roomModel));

        // When
        RoomModel expected = testRoomService.getRoomById("1");

        // Then
        assertEquals(expected, roomModel);
    }

    @Test
    void addRoom() {
        // Given
        RoomModel roomModel3 = new RoomModel(null, "Gürzenich Saal", "Neumarkt 1, 50667 Köln", Category.ORCHESTER_HALL, "Ein traditionsreicher Saal für Konzerte und Veranstaltungen.",
                "154427648", "Ropold", "https://avatars.githubusercontent.com/u/154427648?v=4", "https://github.com/Ropold", true, null);
        RoomModel newRoom = new RoomModel("3", roomModel3.name(), roomModel3.address(), roomModel3.category(), roomModel3.description(),
                roomModel3.appUserGithubId(), roomModel3.appUserUsername(), roomModel3.appUserAvatarUrl(), roomModel3.appUserGithubUrl(),
                roomModel3.isActive(), "https://www.test.de/");

        when(idService.generateRandomId()).thenReturn("3");
        when(roomRepository.save(any(RoomModel.class))).thenReturn(newRoom);

        // When
        RoomModel expected = testRoomService.addRoom(roomModel3);

        // Then
        assertEquals(newRoom, expected);
    }

    @Test
    void updateRoomWithPut() {
        // Given
        RoomModel existingRoom = new RoomModel(
                "1",
                "Gürzenich Saal",
                "Neumarkt 1, 50667 Köln",
                Category.ORCHESTER_HALL,
                "Ein traditionsreicher Saal für Konzerte und Veranstaltungen.",
                "154427648", "Ropold", "https://avatars.githubusercontent.com/u/154427648?v=4",
                "https://github.com/Ropold", true, "https://www.test.de/"
        );

        RoomModel updatedRoom = new RoomModel(
                "1",
                existingRoom.name(),
                existingRoom.address(),
                existingRoom.category(),
                existingRoom.description(),
                existingRoom.appUserGithubId(),
                existingRoom.appUserUsername(),
                existingRoom.appUserAvatarUrl(),
                existingRoom.appUserGithubUrl(),
                false, // Updated isActive value
                "https://www.test.de/"
        );

        when(roomRepository.existsById("1")).thenReturn(true);
        when(roomRepository.save(any(RoomModel.class))).thenReturn(updatedRoom);

        // When
        RoomModel expected = testRoomService.updateRoomWithPut("1", updatedRoom);

        // Then
        assertEquals(updatedRoom, expected);
        verify(roomRepository, times(1)).existsById("1");
        verify(roomRepository, times(1)).save(updatedRoom);
    }

    @Test
    void deleteRoomTest() {
        // Given
        String fixedId = "1";
        when(roomRepository.findById(fixedId)).thenReturn(Optional.of(roomModel));

        // When
        testRoomService.deleteRoom(fixedId);

        // Then
        verify(roomRepository).deleteById(fixedId);
        verify(roomRepository).findById(fixedId);
    }
}
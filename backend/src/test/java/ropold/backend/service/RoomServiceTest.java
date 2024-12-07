package ropold.backend.service;

import org.junit.jupiter.api.BeforeEach;
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
    RoomService testRoomService = new RoomService(idService, roomRepository, cloudinaryService);

    RoomModel roomModel1 = new RoomModel(
            "1", "Gürzenich Saal", "Martinstr. 29 50667 Köln",
            Category.ORCHESTER_HALL, "Ein traditionsreicher Saal für Konzerte und Veranstaltungen.",
            "123", "Testuser", "https://avatars-of-test-user.com/",
            "https://github.com/Testuser", true, "https://res.cloudinary.com/Testuser/image/upload/Testuser/testimage1.jpg"
    );

    RoomModel roomModel2 = new RoomModel(
            "2", "Beethoven-Saal", "Beethovenstraße 1, 53115 Bonn",
            Category.ORCHESTER_HALL, "Ein moderner Saal für klassische Musik.",
            "123", "Testuser", "https://avatars-of-test-user.com/",
            "https://github.com/Testuser", false, "https://res.cloudinary.com/Testuser/image/upload/Testuser/testimage2.jpg"
    );

    List<RoomModel> rooms = List.of(roomModel1, roomModel2);

    @BeforeEach
    void setup() {
        roomRepository.deleteAll();
        roomRepository.saveAll(List.of(roomModel1, roomModel2));
    }

    @Test
    void getActiveRooms() {
        // Given
        List<RoomModel> activeRooms = List.of(roomModel1);
        when(roomRepository.findAll()).thenReturn(rooms);

        // When
        List<RoomModel> expected = testRoomService.getActiveRooms();

        // Then
        assertEquals(expected, activeRooms);
    }

    @Test
    void toggleActiveStatus() {
        // Given
        RoomModel existingRoom = new RoomModel(
                "1", "Gürzenich Saal", "Martinstr. 29 50667 Köln", Category.ORCHESTER_HALL,
                "Ein traditionsreicher Saal für Konzerte und Veranstaltungen.", "123", "Testuser",
                "https://avatars-of-test-user.com/", "https://github.com/Testuser", true,
                "https://res.cloudinary.com/Testuser/image/upload/Testuser/testimage1.jpg"
        );

        RoomModel updatedRoom = new RoomModel(
                "1", existingRoom.name(), existingRoom.address(), existingRoom.category(),
                existingRoom.description(), existingRoom.appUserGithubId(), existingRoom.appUserUsername(),
                existingRoom.appUserAvatarUrl(), existingRoom.appUserGithubUrl(), false,
                existingRoom.imageUrl()
        );

        when(roomRepository.findById("1")).thenReturn(Optional.of(existingRoom));
        when(roomRepository.save(any(RoomModel.class))).thenReturn(updatedRoom);

        // When
        RoomModel expected = testRoomService.toggleActiveStatus("1");

        // Then
        assertEquals(updatedRoom, expected);
        verify(roomRepository).findById("1");
        verify(roomRepository).save(updatedRoom);
    }

    @Test
    void getRoomsByIds() {
        // Given
        List<String> roomIds = List.of("1", "2");
        when(roomRepository.findAllById(roomIds)).thenReturn(rooms);

        // When
        List<RoomModel> expected = testRoomService.getRoomsByIds(roomIds);

        // Then
        assertEquals(expected, rooms);
    }

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
        when(roomRepository.findById("1")).thenReturn(Optional.of(roomModel1));

        // When
        RoomModel expected = testRoomService.getRoomById("1");

        // Then
        assertEquals(expected, roomModel1);
    }

    @Test
    void addRoom() {
        // Given
        RoomModel roomModel3 = new RoomModel(
                null, "Großer Saal", "Neumarkt 1, 50667 Köln", Category.ORCHESTER_HALL,
                "Ein traditionsreicher Saal für Konzerte und Veranstaltungen.", "123", "Testuser",
                "https://avatars-of-test-user.com/", "https://github.com/Testuser", true, null
        );
        RoomModel newRoom = new RoomModel("3", roomModel3.name(), roomModel3.address(), roomModel3.category(), roomModel3.description(),
                roomModel3.appUserGithubId(), roomModel3.appUserUsername(), roomModel3.appUserAvatarUrl(),
                roomModel3.appUserGithubUrl(), roomModel3.isActive(), "https://res.cloudinary.com/Testuser/image/upload/Testuser/testimage3.jpg");

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
                "1", "Gürzenich Saal", "Neumarkt 1, 50667 Köln", Category.ORCHESTER_HALL,
                "Ein traditionsreicher Saal für Konzerte und Veranstaltungen.", "123", "Testuser",
                "https://avatars-of-test-user.com/", "https://github.com/Testuser", true, "https://www.test.de/"
        );

        RoomModel updatedRoom = new RoomModel(
                "1", existingRoom.name(), existingRoom.address(), existingRoom.category(),
                existingRoom.description(), existingRoom.appUserGithubId(), existingRoom.appUserUsername(),
                existingRoom.appUserAvatarUrl(), existingRoom.appUserGithubUrl(), false, "https://www.test.de/"
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
    void deleteRoomWithImage() {
        // Given
        String fixedId = "1";
        when(roomRepository.findById(fixedId)).thenReturn(Optional.of(roomModel1));

        // When
        testRoomService.deleteRoom(fixedId);

        // Then
        verify(roomRepository).deleteById(fixedId);
        verify(roomRepository).findById(fixedId);
        verify(cloudinaryService).deleteImage(roomModel1.imageUrl());
    }

    @Test
    void deleteRoomWithoutImage() {
        // Given
        RoomModel roomWithoutImage = new RoomModel(
                "2", "Beethoven-Saal", "Beethovenstraße 1, 53115 Bonn", Category.ORCHESTER_HALL,
                "Ein moderner Saal für klassische Musik.", "123", "Testuser",
                "https://avatars-of-test-user.com/", "https://github.com/Testuser", false, null
        );

        String fixedId = "2";
        when(roomRepository.findById(fixedId)).thenReturn(Optional.of(roomWithoutImage));

        // When
        testRoomService.deleteRoom(fixedId);

        // Then
        verify(roomRepository).deleteById(fixedId);
        verify(roomRepository).findById(fixedId);
        verify(cloudinaryService, never()).deleteImage(any());
    }
}
package ropold.backend.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ropold.backend.model.RoomModel;
import ropold.backend.repository.RoomRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@SpringBootTest
class RoomServiceTest {
    IdService idService = mock(IdService.class);
    RoomRepository roomRepository = mock(RoomRepository.class);
    CloudinaryService cloudinaryService = mock(CloudinaryService.class);
    RoomService testRoomService = new RoomService(idService, roomRepository,cloudinaryService);

    RoomModel roomModel = new RoomModel("1", "Gürzenich Saal", "Neumarkt 1, 50667 Köln", "Orchester-Saal", "Ein traditionsreicher Saal für Konzerte und Veranstaltungen.", WishlistStatus.NOT_ON_WISHLIST, "https://www.test.de/");
    RoomModel roomModel2 = new RoomModel("2", "Gürzenich Saal", "Neumarkt 1, 50667 Köln", "Orchester-Saal", "Ein traditionsreicher Saal für Konzerte und Veranstaltungen.", WishlistStatus.NOT_ON_WISHLIST, "https://www.test.de/");
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
        RoomModel roomModel3 = new RoomModel("3", "Gürzenich Saal", "Neumarkt 1, 50667 Köln", "Orchester-Saal", "Ein traditionsreicher Saal für Konzerte und Veranstaltungen.", WishlistStatus.NOT_ON_WISHLIST, "https://www.test.de/");
        RoomModel newRoom = new RoomModel("3", roomModel3.name(), roomModel3.address(), roomModel3.category(), roomModel3.description(), roomModel3.wishlistStatus(), roomModel3.imageUrl());

        when(idService.generateRandomId()).thenReturn("3");
        when(roomRepository.save(any(RoomModel.class))).thenReturn(newRoom);

        // When
        RoomModel expected = testRoomService.addRoom(roomModel3);

        // Then
        assertEquals(newRoom, expected);
        assertEquals(roomModel3.name(), expected.name());
        assertEquals(roomModel3.address(), expected.address());
        assertEquals(roomModel3.category(), expected.category());
        assertEquals(roomModel3.description(), expected.description());
        assertEquals(roomModel3.wishlistStatus(), expected.wishlistStatus());
    }

    @Test
    void updateRoomWithPut() {
        // Given
        RoomModel existingRoom = new RoomModel(
                "1",
                "Gürzenich Saal",
                "Neumarkt 1, 50667 Köln",
                "Orchester-Saal",
                "Ein traditionsreicher Saal für Konzerte und Veranstaltungen.",
                WishlistStatus.NOT_ON_WISHLIST,
                "https://www.test.de/"
        );

        RoomModel updatedRoom = new RoomModel(
                "1",
                existingRoom.name(),
                existingRoom.address(),
                existingRoom.category(),
                existingRoom.description(),
                WishlistStatus.ON_WISHLIST,
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
        String fixedId = "123e4567-e89b-12d3-a456-426614174000";
        RoomRepository roomRepositoryMock = mock(RoomRepository.class);
        CloudinaryService cloudinaryServiceMock = mock(CloudinaryService.class);

        RoomModel room = new RoomModel(fixedId, "Test Room", "Test Address", "Test Category", "Test Description", WishlistStatus.ON_WISHLIST, null);
        when(roomRepositoryMock.findById(fixedId)).thenReturn(Optional.of(room));

        RoomService roomService = new RoomService(new IdService(), roomRepositoryMock, cloudinaryServiceMock);

        roomService.deleteRoom(fixedId);

        verify(roomRepositoryMock).deleteById(fixedId);
        verify(roomRepositoryMock).findById(fixedId);
    }

}

package ropold.backend.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ropold.backend.model.RoomModel;
import ropold.backend.model.WishlistStatus;
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
    RoomService roomService = new RoomService(idService, roomRepository,cloudinaryService);

    RoomModel roomModel = new RoomModel("1", "Gürzenich Saal", "Neumarkt 1, 50667 Köln", "Orchester-Saal", "Ein traditionsreicher Saal für Konzerte und Veranstaltungen.", WishlistStatus.NOT_ON_WISHLIST, "https://www.test.de/");
    RoomModel roomModel2 = new RoomModel("2", "Gürzenich Saal", "Neumarkt 1, 50667 Köln", "Orchester-Saal", "Ein traditionsreicher Saal für Konzerte und Veranstaltungen.", WishlistStatus.NOT_ON_WISHLIST, "https://www.test.de/");
    List<RoomModel> rooms = List.of(roomModel, roomModel2);

    @Test
    void getAllRooms() {
        // Given
        when(roomRepository.findAll()).thenReturn(rooms);

        // When
        List<RoomModel> expected = roomService.getAllRooms();

        // Then
        assertEquals(expected, rooms);
    }

    @Test
    void getRoomById() {
        // Given
        when(roomRepository.findById("1")).thenReturn(Optional.of(roomModel));

        // When
        RoomModel expected = roomService.getRoomById("1");

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
        RoomModel expected = roomService.addRoom(roomModel3);

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
        RoomModel expected = roomService.updateRoomWithPut("1", updatedRoom);

        // Then
        assertEquals(updatedRoom, expected);
        verify(roomRepository, times(1)).existsById("1");
        verify(roomRepository, times(1)).save(updatedRoom);
    }

    @Test
    void deleteRoom() {
        // Given
        String roomId = "1";
        when(roomRepository.existsById(roomId)).thenReturn(true);

        // When
        roomService.deleteRoom(roomId);

        // Then
        verify(roomRepository, times(1)).deleteById(roomId);
        verify(roomRepository, times(1)).existsById(roomId);
        verifyNoMoreInteractions(roomRepository);
    }


}

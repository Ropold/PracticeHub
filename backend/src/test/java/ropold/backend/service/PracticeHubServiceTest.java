package ropold.backend.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ropold.backend.model.RoomModel;
import ropold.backend.model.WishlistStatus;
import ropold.backend.repository.PracticeHubRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@SpringBootTest
class PracticeHubServiceTest {
    IdService idService = mock(IdService.class);
    PracticeHubRepository practiceHubRepository = mock(PracticeHubRepository.class);
    PracticeHubService practiceHubService = new PracticeHubService(idService, practiceHubRepository);

    RoomModel roomModel = new RoomModel("1", "Gürzenich Saal", "Neumarkt 1, 50667 Köln", "Orchester-Saal", "Ein traditionsreicher Saal für Konzerte und Veranstaltungen.", WishlistStatus.NOT_ON_WISHLIST);
    RoomModel roomModel2 = new RoomModel("2", "Gürzenich Saal", "Neumarkt 1, 50667 Köln", "Orchester-Saal", "Ein traditionsreicher Saal für Konzerte und Veranstaltungen.", WishlistStatus.NOT_ON_WISHLIST);
    List<RoomModel> rooms = List.of(roomModel, roomModel2);

    @Test
    void getAllRooms() {
        // Given
        when(practiceHubRepository.findAll()).thenReturn(rooms);

        // When
        List<RoomModel> expected = practiceHubService.getAllRooms();

        // Then
        assertEquals(expected, rooms);
    }

    @Test
    void getRoomById() {
        // Given
        when(practiceHubRepository.findById("1")).thenReturn(Optional.of(roomModel));

        // When
        RoomModel expected = practiceHubService.getRoomById("1");

        // Then
        assertEquals(expected, roomModel);
    }

    @Test
    void addRoom() {
        // Given
        RoomModel roomModel3 = new RoomModel("3", "Gürzenich Saal", "Neumarkt 1, 50667 Köln", "Orchester-Saal", "Ein traditionsreicher Saal für Konzerte und Veranstaltungen.", WishlistStatus.NOT_ON_WISHLIST);
        RoomModel newRoom = new RoomModel("3", roomModel3.name(), roomModel3.address(), roomModel3.category(), roomModel3.description(), roomModel3.wishlistStatus());

        when(idService.generateRandomId()).thenReturn("3");
        when(practiceHubRepository.save(any(RoomModel.class))).thenReturn(newRoom);

        // When
        RoomModel expected = practiceHubService.addRoom(roomModel3);

        // Then
        assertEquals(newRoom, expected);
        assertEquals(roomModel3.name(), expected.name());
        assertEquals(roomModel3.address(), expected.address());
        assertEquals(roomModel3.category(), expected.category());
        assertEquals(roomModel3.description(), expected.description());
        assertEquals(roomModel3.wishlistStatus(), expected.wishlistStatus());
    }
}

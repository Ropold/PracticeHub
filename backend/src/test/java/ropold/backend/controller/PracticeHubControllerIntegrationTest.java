package ropold.backend.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ropold.backend.model.RoomModel;
import ropold.backend.model.WishlistStatus;
import ropold.backend.repository.PracticeHubRepository;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PracticeHubControllerIntegrationTest {
    static RoomModel roomModel;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    PracticeHubRepository practiceHubRepository;

    @BeforeEach
    void setup() {
        practiceHubRepository.deleteAll();

        roomModel = new RoomModel("1", "Gürzenich Saal", "Neumarkt 1, 50667 Köln",
                "Orchester-Saal", "Ein traditionsreicher Saal für Konzerte und Veranstaltungen.",
                WishlistStatus.ON_WISHLIST);

        practiceHubRepository.save(roomModel);
    }

    @Test
    void getAllRooms_expectListWithOneRoom_whenOneRoomSaved() throws Exception {

        // WHEN
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/practice-hub")
                )
                // THEN
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("""
                                                                                       [ 
                                                                                         {
                            "id": "1",
                            "name": "Gürzenich Saal",
                            "address": "Neumarkt 1, 50667 Köln",
                            "category": "Orchester-Saal",
                            "description": "Ein traditionsreicher Saal für Konzerte und Veranstaltungen.",
                            "wishlistStatus": "ON_WISHLIST"
                                                                                         }]
                        """
                ));
    }

    @Test
    void getRoomById_returnRoomWithId1_whenRoomWithId1Saved() throws Exception {

        // WHEN
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/practice-hub/1")
                )
                // THEN
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("""
                                                                                         {
                            "id": "1",
                            "name": "Gürzenich Saal",
                            "address": "Neumarkt 1, 50667 Köln",
                            "category": "Orchester-Saal",
                            "description": "Ein traditionsreicher Saal für Konzerte und Veranstaltungen.",
                            "wishlistStatus": "ON_WISHLIST"
                                                                                         }
                        """
                ));
    }

    @Test
    void postRoom_shouldReturnSavedRoom() throws Exception {
        // GIVEN
        practiceHubRepository.deleteAll();

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.post("/api/practice-hub")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                         {
                            "name": "Gürzenich Saal",
                            "address": "Neumarkt 1, 50667 Köln",
                            "category": "Orchester-Saal",
                            "description": "Ein traditionsreicher Saal für Konzerte und Veranstaltungen.",
                            "wishlistStatus": "ON_WISHLIST"
                         }
                        """)
        ).andExpect(status().isCreated());

        // THEN
        List<RoomModel> allRooms = practiceHubRepository.findAll();
        Assertions.assertEquals(1, allRooms.size());

        RoomModel savedRoom = allRooms.get(0);
        org.assertj.core.api.Assertions.assertThat(savedRoom)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(new RoomModel(
                        null,
                        "Gürzenich Saal",
                        "Neumarkt 1, 50667 Köln",
                        "Orchester-Saal",
                        "Ein traditionsreicher Saal für Konzerte und Veranstaltungen.",
                        WishlistStatus.ON_WISHLIST
                ));
    }

    @Test
    void updateRoomWithPut_shouldUpdateWishlistStatus() throws Exception {
        // GIVEN
        RoomModel existingRoom = new RoomModel("1", "Gürzenich Saal", "Neumarkt 1, 50667 Köln",
                "Orchester-Saal", "Ein traditionsreicher Saal für Konzerte und Veranstaltungen.",
                WishlistStatus.NOT_ON_WISHLIST);
        practiceHubRepository.save(existingRoom);

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.put("/api/practice-hub/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                     {
                        "id": "1",
                        "name": "Gürzenich Saal",
                        "address": "Neumarkt 1, 50667 Köln",
                        "category": "Orchester-Saal",
                        "description": "Ein traditionsreicher Saal für Konzerte und Veranstaltungen.",
                        "wishlistStatus": "ON_WISHLIST"
                     }
                     """)
                )
                // THEN
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("""
                                                             {
                                                                "id": "1",
                                                                "name": "Gürzenich Saal",
                                                                "address": "Neumarkt 1, 50667 Köln",
                                                                "category": "Orchester-Saal",
                                                                "description": "Ein traditionsreicher Saal für Konzerte und Veranstaltungen.",
                                                                "wishlistStatus": "ON_WISHLIST"
                                                             }
                                                             """));

        // Verify in repository
        RoomModel updatedRoom = practiceHubRepository.findById("1").orElseThrow();
        Assertions.assertEquals(WishlistStatus.ON_WISHLIST, updatedRoom.wishlistStatus());
    }

    @Test
    void deleteRoom_shouldRemoveRoomFromRepository() throws Exception {
        // GIVEN
        RoomModel roomToDelete = new RoomModel("2", "Beethoven-Saal", "Beethovenstraße 1, 53115 Bonn",
                "Konzerthalle", "Ein moderner Saal für klassische Musik und Veranstaltungen.",
                WishlistStatus.ON_WISHLIST);
        practiceHubRepository.save(roomToDelete);

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/practice-hub/2"))
                // THEN
                .andExpect(status().isNoContent());

        // Verify repository is empty
        Assertions.assertFalse(practiceHubRepository.existsById("2"));
    }
}

package ropold.backend.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ropold.backend.model.RoomModel;
import ropold.backend.model.WishlistStatus;
import ropold.backend.repository.RoomRepository;


import java.io.File;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RoomControllerIntegrationTest {
    static RoomModel roomModel;

    @MockBean
    private Cloudinary cloudinary;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    RoomRepository roomRepository;


    @BeforeEach
    void setup() {
        roomRepository.deleteAll();

        roomModel = new RoomModel("1", "Gürzenich Saal", "Neumarkt 1, 50667 Köln",
                "Orchester-Saal", "Ein traditionsreicher Saal für Konzerte und Veranstaltungen.",
                WishlistStatus.ON_WISHLIST, "https://www.test.de/");

        roomRepository.save(roomModel);
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
                            "wishlistStatus": "ON_WISHLIST",
                            "imageUrl": "https://www.test.de/"
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
                            "wishlistStatus": "ON_WISHLIST",
                            "imageUrl": "https://www.test.de/"
                                                                                         }
                        """
                ));
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    void postRoom_shouldReturnSavedRoom() throws Exception {
        // GIVEN
        roomRepository.deleteAll();
        Uploader mockuploader = mock(Uploader.class);
        when(mockuploader.upload(any(), anyMap())).thenReturn(Map.of("secure_url", "https://www.test.de/"));
        when(cloudinary.uploader()).thenReturn(mockuploader);

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/practice-hub")
                .file(new MockMultipartFile("image", "test.jpg", "image/jpeg", "test image".getBytes()))
                .file(new MockMultipartFile("roomModelDto", "","application/json", """
                        {
                            "name": "Gürzenich Saal",
                            "address": "Neumarkt 1, 50667 Köln",
                            "category": "Orchester-Saal",
                            "description": "Ein traditionsreicher Saal für Konzerte und Veranstaltungen.",
                            "wishlistStatus": "ON_WISHLIST"
                        }
                        """.getBytes())))
                .andExpect(status().isCreated());

        // THEN
        List<RoomModel> allRooms = roomRepository.findAll();
        Assertions.assertEquals(1, allRooms.size());

        RoomModel savedRoom = allRooms.getFirst();
        org.assertj.core.api.Assertions.assertThat(savedRoom)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(new RoomModel(
                        null,
                        "Gürzenich Saal",
                        "Neumarkt 1, 50667 Köln",
                        "Orchester-Saal",
                        "Ein traditionsreicher Saal für Konzerte und Veranstaltungen.",
                        WishlistStatus.ON_WISHLIST,
                        "https://www.test.de/"
                ));
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    void updateRoomWithPut_shouldUpdateWishlistStatus() throws Exception {
        // GIVEN
        RoomModel existingRoom = new RoomModel("1", "Gürzenich Saal", "Neumarkt 1, 50667 Köln",
                "Orchester-Saal", "Ein traditionsreicher Saal für Konzerte und Veranstaltungen.",
                WishlistStatus.NOT_ON_WISHLIST, "https://www.test.de/");
        roomRepository.save(existingRoom);

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
        RoomModel updatedRoom = roomRepository.findById("1").orElseThrow();
        Assertions.assertEquals(WishlistStatus.ON_WISHLIST, updatedRoom.wishlistStatus());
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    void deleteRoom_shouldRemoveRoomFromRepository() throws Exception {
        // GIVEN
        RoomModel roomToDelete = new RoomModel("2", "Beethoven-Saal", "Beethovenstraße 1, 53115 Bonn",
                "Konzerthalle", "Ein moderner Saal für klassische Musik und Veranstaltungen.",
                WishlistStatus.ON_WISHLIST, "https://www.test.de/");
        roomRepository.save(roomToDelete);

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/practice-hub/2"))
                // THEN
                .andExpect(status().isNoContent());

        // Verify repository is empty
        Assertions.assertFalse(roomRepository.existsById("2"));
    }
}

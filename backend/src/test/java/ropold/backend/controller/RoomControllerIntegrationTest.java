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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ropold.backend.model.AppUser;
import ropold.backend.model.Category;
import ropold.backend.model.RoomModel;
import ropold.backend.repository.AppUserRepository;
import ropold.backend.repository.RoomRepository;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RoomControllerIntegrationTest {

    @MockBean
    private Cloudinary cloudinary;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @BeforeEach
    void setup() {
        // Bereinige die Datenbank
        roomRepository.deleteAll();
        appUserRepository.deleteAll();

        // Erstelle Räume
        RoomModel room1 = new RoomModel(
                "1", "Gürzenich Saal", "Martinstr. 29 50667 Köln",
                Category.ORCHESTER_HALL, "Ein traditionsreicher Saal für Konzerte und Veranstaltungen.",
                "123", "Ropold", "https://avatars.githubusercontent.com/u/154427648?v=4",
                "https://github.com/Ropold", true, "https://res.cloudinary.com/dzjjlydk3/image/upload/v1733477653/sqw62kggomno2bufjaoi.jpg");

        RoomModel room2 = new RoomModel(
                "2", "Beethoven-Saal", "Beethovenstraße 1, 53115 Bonn",
                Category.ORCHESTER_HALL, "Ein moderner Saal für klassische Musik.",
                "123", "Ropold", "https://avatars.githubusercontent.com/u/154427648?v=4",
                "https://github.com/Ropold", true, "https://res.cloudinary.com/dzjjlydk3/image/upload/v1733477653/sqw62kggomno2bufjaoi.jpg");

        roomRepository.saveAll(List.of(room1, room2));

        // Erstelle Benutzer mit Favoriten
        AppUser user = new AppUser(
                "123",
                "Ropold",
                "Ropold Name",
                "https://avatars.githubusercontent.com/u/154427648?v=4",
                "https://github.com/Ropold",
                List.of("1") // Favorit ist nur Raum "1"
        );
        appUserRepository.save(user);
    }

    @Test
    @WithMockUser(username = "123")
    void getUserFavorites_shouldReturnFavoriteRooms() throws Exception {
        mockMvc.perform(get("/api/practice-hub/favorites/123"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                [
                    {
                        "id": "1",
                        "name": "Gürzenich Saal",
                        "address": "Martinstr. 29 50667 Köln",
                        "category": "ORCHESTER_HALL",
                        "description": "Ein traditionsreicher Saal für Konzerte und Veranstaltungen.",
                        "appUserGithubId": "123",
                        "appUserUsername": "Ropold",
                        "appUserAvatarUrl": "https://avatars.githubusercontent.com/u/154427648?v=4",
                        "appUserGithubUrl": "https://github.com/Ropold",
                        "isActive": true,
                        "imageUrl": "https://res.cloudinary.com/dzjjlydk3/image/upload/v1733477653/sqw62kggomno2bufjaoi.jpg"
                    }
                ]
                """));
    }

    @Test
    @WithMockUser(username = "123")
    void addRoomToFavorites_shouldAddRoomToFavoritesAndReturnCreated() throws Exception {
        // GIVEN: Der Raum mit der ID "2" existiert bereits

        // WHEN: Der Raum mit der ID "2" wird zu den Favoriten des Benutzers hinzugefügt
        mockMvc.perform(MockMvcRequestBuilders.post("/api/practice-hub/favorites/123/2"))
                .andExpect(status().isCreated());

        // THEN: Der Benutzer sollte jetzt Raum "2" in seinen Favoriten haben
        AppUser updatedUser = appUserRepository.findById("123").orElseThrow();
        Assertions.assertTrue(updatedUser.favorites().contains("2"));
    }

    @Test
    @WithMockUser(username = "123")
    void removeRoomFromFavorites_expectNoContent_whenRoomRemoved() throws Exception {
        // GIVEN: Der Benutzer hat den Raum in seinen Favoriten
        AppUser user = appUserRepository.findById("123").orElseThrow();
        List<String> favoritesBefore = user.favorites();  // Favoriten vor der Anfrage
        Assertions.assertTrue(favoritesBefore.contains("1")); // Sicherstellen, dass Raum "1" in den Favoriten ist

        // WHEN: Raum aus den Favoriten entfernen

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/practice-hub/favorites/{userId}/{roomId}", "123", "1"))
                .andExpect(status().isNoContent());

        // THEN: Der Raum sollte nicht mehr in den Favoriten des Benutzers sein
        AppUser updatedUser = appUserRepository.findById("123").orElseThrow();
        List<String> favoritesAfter = updatedUser.favorites();
        Assertions.assertFalse(favoritesAfter.contains("1")); // Sicherstellen, dass Raum "1" entfernt wurde
    }

    @Test
    void getAllRooms_expectListWithTwoRooms_whenTwoRoomsSaved() throws Exception {
        // WHEN
        mockMvc.perform(get("/api/practice-hub"))
                // THEN
                .andExpect(status().isOk())
                .andExpect(content().json("""
                [
                    {
                        "id": "1",
                        "name": "Gürzenich Saal",
                        "address": "Martinstr. 29 50667 Köln",
                        "category": "ORCHESTER_HALL",
                        "description": "Ein traditionsreicher Saal für Konzerte und Veranstaltungen.",
                        "appUserGithubId": "123",
                        "appUserUsername": "Ropold",
                        "appUserAvatarUrl": "https://avatars.githubusercontent.com/u/154427648?v=4",
                        "appUserGithubUrl": "https://github.com/Ropold",
                        "isActive": true,
                        "imageUrl": "https://res.cloudinary.com/dzjjlydk3/image/upload/v1733477653/sqw62kggomno2bufjaoi.jpg"
                    },
                    {
                        "id": "2",
                        "name": "Beethoven-Saal",
                        "address": "Beethovenstraße 1, 53115 Bonn",
                        "category": "ORCHESTER_HALL",
                        "description": "Ein moderner Saal für klassische Musik.",
                        "appUserGithubId": "123",
                        "appUserUsername": "Ropold",
                        "appUserAvatarUrl": "https://avatars.githubusercontent.com/u/154427648?v=4",
                        "appUserGithubUrl": "https://github.com/Ropold",
                        "isActive": true,
                        "imageUrl": "https://res.cloudinary.com/dzjjlydk3/image/upload/v1733477653/sqw62kggomno2bufjaoi.jpg"
                    }
                ]
            """));
    }


    @Test
    void getRoomById_returnRoomWithId1_whenRoomWithId1Saved() throws Exception {
        // WHEN
        mockMvc.perform(get("/api/practice-hub/1"))
                // THEN
                .andExpect(status().isOk())
                .andExpect(content().json("""
                    {
                        "id": "1",
                        "name": "Gürzenich Saal",
                        "address": "Martinstr. 29 50667 Köln",
                        "category": "ORCHESTER_HALL",
                        "description": "Ein traditionsreicher Saal für Konzerte und Veranstaltungen.",
                        "appUserGithubId": "123",
                        "appUserUsername": "Ropold",
                        "appUserAvatarUrl": "https://avatars.githubusercontent.com/u/154427648?v=4",
                        "appUserGithubUrl": "https://github.com/Ropold",
                        "isActive": true,
                        "imageUrl": "https://res.cloudinary.com/dzjjlydk3/image/upload/v1733477653/sqw62kggomno2bufjaoi.jpg"
                    }
                    """));
    }


    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    void postRoom_shouldReturnSavedRoom() throws Exception {
        // GIVEN
        roomRepository.deleteAll();
        Uploader mockUploader = mock(Uploader.class);
        when(mockUploader.upload(any(), anyMap())).thenReturn(Map.of("secure_url", "https://www.test.de/"));
        when(cloudinary.uploader()).thenReturn(mockUploader);

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/practice-hub")
                        .file(new MockMultipartFile("image", "test.jpg", "image/jpeg", "test image".getBytes()))
                        .file(new MockMultipartFile("roomModelDto", "", "application/json", """
                    {
                        "name": "Beethoven-Saal",
                        "address": "Beethovenstraße 1, 53115 Bonn",
                        "category": "ORCHESTER_HALL",
                        "description": "Ein moderner Saal für klassische Musik.",
                        "appUserGithubId": "123",
                        "appUserUsername": "testUser",
                        "appUserAvatarUrl": "https://avatars.example.com/testUser",
                        "appUserGithubUrl": "https://github.com/testUser",
                        "isActive": true
                    }
                    """.getBytes())))
                .andExpect(status().isCreated());

        // THEN
        List<RoomModel> allRooms = roomRepository.findAll();
        Assertions.assertEquals(1, allRooms.size());
        RoomModel savedRoom = allRooms.get(0);
        org.assertj.core.api.Assertions.assertThat(savedRoom)
                .usingRecursiveComparison()
                .ignoringFields("id", "imageUrl") // Ignoring generated fields
                .isEqualTo(new RoomModel(
                        null,
                        "Beethoven-Saal",
                        "Beethovenstraße 1, 53115 Bonn",
                        Category.ORCHESTER_HALL,
                        "Ein moderner Saal für klassische Musik.",
                        "123",
                        "testUser",
                        "https://avatars.example.com/testUser",
                        "https://github.com/testUser",
                        true,
                        null // imageUrl will be set by Cloudinary
                ));
    }


    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    void updateRoomWithPut_shouldUpdateRoomDetails() throws Exception {
        // GIVEN
        RoomModel existingRoom = new RoomModel("1", "Gürzenich Saal", "Martinstr. 29 50667 Köln",
                Category.ORCHESTER_HALL, "Ein traditionsreicher Saal für Konzerte und Veranstaltungen.",
                "123", "Ropold", "https://avatars.githubusercontent.com/u/154427648?v=4",
                "https://github.com/Ropold", true, "https://res.cloudinary.com/dzjjlydk3/image/upload/v1733477653/sqw62kggomno2bufjaoi.jpg");
        roomRepository.save(existingRoom);
        Uploader mockUploader = mock(Uploader.class);
        when(mockUploader.upload(any(), anyMap())).thenReturn(Map.of("secure_url", "https://www.updated-image.com/"));
        when(cloudinary.uploader()).thenReturn(mockUploader);

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/practice-hub/1")
                        .file(new MockMultipartFile("image", "test.jpg", "image/jpeg", "test image".getBytes()))
                        .file(new MockMultipartFile("roomModelDto", "", "application/json", """
                    {
                        "name": "Updated Saal",
                        "address": "Updatedstraße 1, 12345 Stadt",
                        "category": "ORCHESTER_HALL",
                        "description": "Ein aktualisierter Saal für Konzerte.",
                        "appUserGithubId": "123",
                        "appUserUsername": "RopoldUpdated",
                        "appUserAvatarUrl": "https://avatars.githubusercontent.com/u/154427648_updated?v=4",
                        "appUserGithubUrl": "https://github.com/RopoldUpdated",
                        "isActive": false
                    }
                    """.getBytes()))
                        .contentType("multipart/form-data")
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                    {
                        "id": "1",
                        "name": "Updated Saal",
                        "address": "Updatedstraße 1, 12345 Stadt",
                        "category": "ORCHESTER_HALL",
                        "description": "Ein aktualisierter Saal für Konzerte.",
                        "appUserGithubId": "123",
                        "appUserUsername": "RopoldUpdated",
                        "appUserAvatarUrl": "https://avatars.githubusercontent.com/u/154427648_updated?v=4",
                        "appUserGithubUrl": "https://github.com/RopoldUpdated",
                        "isActive": false,
                        "imageUrl": "https://www.updated-image.com/"
                    }
                    """));

        // THEN
        RoomModel updatedRoom = roomRepository.findById("1").orElseThrow();
        Assertions.assertFalse(updatedRoom.isActive());
        Assertions.assertEquals("https://www.updated-image.com/", updatedRoom.imageUrl());
    }


    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    void deleteRoom_shouldRemoveRoomFromRepository() throws Exception {
        // GIVEN
        RoomModel roomToDelete = new RoomModel("2", "Beethoven-Saal", "Beethovenstraße 1, 53115 Bonn",
                Category.ORCHESTER_HALL, "Ein moderner Saal für klassische Musik.",
                "123", "testUser", "https://avatars.example.com/testUser",
                "https://github.com/testUser", true, "https://www.test.de/");
        roomRepository.save(roomToDelete);
        Uploader mockUploader = mock(Uploader.class);
        when(mockUploader.destroy(any(), anyMap())).thenReturn(Map.of("result", "ok"));
        when(cloudinary.uploader()).thenReturn(mockUploader);

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/practice-hub/2"))
                .andExpect(status().isNoContent());

        // THEN
        Assertions.assertFalse(roomRepository.existsById("2"));
    }
}
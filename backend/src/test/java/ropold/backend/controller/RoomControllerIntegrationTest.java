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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ropold.backend.model.AppUser;
import ropold.backend.model.Category;
import ropold.backend.model.RoomModel;
import ropold.backend.repository.AppUserRepository;
import ropold.backend.repository.RoomRepository;

import java.util.Collections;
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
        roomRepository.deleteAll();
        appUserRepository.deleteAll();

        RoomModel room1 = new RoomModel(
                "1", "Gürzenich Saal", "Martinstr. 29 50667 Köln",
                Category.ORCHESTER_HALL, "Ein traditionsreicher Saal für Konzerte und Veranstaltungen.",
                "123", "Testuser", "https://avatars-of-test-user.com/",
                "https://github.com/Testuser", true, "https://res.cloudinary.com/Testuser/image/upload/Testuser/testimage1.jpg");

        RoomModel room2 = new RoomModel(
                "2", "Beethoven-Saal", "Beethovenstraße 1, 53115 Bonn",
                Category.ORCHESTER_HALL, "Ein moderner Saal für klassische Musik.",
                "123", "Testuser", "https://avatars-of-test-user.com/",
                "https://github.com/Testuser", true, "https://res.cloudinary.com/Testuser/image/upload/Testuser/testimage2.jpg");

        roomRepository.saveAll(List.of(room1, room2));

        AppUser user = new AppUser(
                "123",
                "Testuser",
                "Max Mustermann",
                "https://avatars-of-test-user.com/",
                "https://github.com/Testuser",
                List.of("1")
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
                        "appUserUsername": "Testuser",
                        "appUserAvatarUrl": "https://avatars-of-test-user.com/",
                        "appUserGithubUrl": "https://github.com/Testuser",
                        "isActive": true,
                        "imageUrl": "https://res.cloudinary.com/Testuser/image/upload/Testuser/testimage1.jpg"
                    }
                ]
                """));
    }

    @Test
    void addRoomToFavorites_shouldAddRoomToFavoritesAndReturnCreated() throws Exception {
        // Mock OAuth2User
        OAuth2User mockOAuth2User = mock(OAuth2User.class);
        when(mockOAuth2User.getName()).thenReturn("123");  // The name of the authenticated user

        // Set the Mock OAuth2User in the SecurityContext and mark it as authenticated
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(mockOAuth2User, null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/practice-hub/favorites/123/2"))
                .andExpect(status().isCreated());  // Expecting the "201 Created" status

        AppUser updatedUser = appUserRepository.findById("123").orElseThrow();
        Assertions.assertTrue(updatedUser.favorites().contains("2"));
    }


    @Test
    void removeRoomFromFavorites_expectNoContent_whenRoomRemoved() throws Exception {
        // GIVEN

        OAuth2User mockOAuth2User = mock(OAuth2User.class);
        when(mockOAuth2User.getName()).thenReturn("123");  // The name of the authenticated user

        // Set the Mock OAuth2User in the SecurityContext and mark it as authenticated
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(mockOAuth2User, null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
        );

        AppUser user = appUserRepository.findById("123").orElseThrow();
        List<String> favoritesBefore = user.favorites();
        Assertions.assertTrue(favoritesBefore.contains("1"));

        // WHEN:
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/practice-hub/favorites/{userId}/{roomId}", "123", "1"))
                .andExpect(status().isNoContent());

        // THEN:
        AppUser updatedUser = appUserRepository.findById("123").orElseThrow();
        List<String> favoritesAfter = updatedUser.favorites();
        Assertions.assertFalse(favoritesAfter.contains("1"));
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
                        "appUserUsername": "Testuser",
                        "appUserAvatarUrl": "https://avatars-of-test-user.com/",
                        "appUserGithubUrl": "https://github.com/Testuser",
                        "isActive": true,
                        "imageUrl": "https://res.cloudinary.com/Testuser/image/upload/Testuser/testimage1.jpg"
                    },
                    {
                        "id": "2",
                        "name": "Beethoven-Saal",
                        "address": "Beethovenstraße 1, 53115 Bonn",
                        "category": "ORCHESTER_HALL",
                        "description": "Ein moderner Saal für klassische Musik.",
                        "appUserGithubId": "123",
                        "appUserUsername": "Testuser",
                        "appUserAvatarUrl": "https://avatars-of-test-user.com/",
                        "appUserGithubUrl": "https://github.com/Testuser",
                        "isActive": true,
                        "imageUrl": "https://res.cloudinary.com/Testuser/image/upload/Testuser/testimage2.jpg"
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
                        "appUserUsername": "Testuser",
                        "appUserAvatarUrl": "https://avatars-of-test-user.com/",
                        "appUserGithubUrl": "https://github.com/Testuser",
                        "isActive": true,
                        "imageUrl": "https://res.cloudinary.com/Testuser/image/upload/Testuser/testimage1.jpg"
                    }
                    """));
    }

    @Test
    @WithMockUser(username = "123")
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
        RoomModel savedRoom = allRooms.getFirst();
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
                        null
                ));
    }

    @Test
    void updateRoomWithPut_shouldUpdateRoomDetails() throws Exception {
        // GIVEN

        OAuth2User mockOAuth2User = mock(OAuth2User.class);
        when(mockOAuth2User.getName()).thenReturn("123");  // The name of the authenticated user

        // Set the Mock OAuth2User in the SecurityContext and mark it as authenticated
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(mockOAuth2User, null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
        );

        RoomModel existingRoom = new RoomModel("1", "Gürzenich Saal", "Martinstr. 29 50667 Köln",
                Category.ORCHESTER_HALL, "Ein traditionsreicher Saal für Konzerte und Veranstaltungen.",
                "123", "TestUser", "https://avatars-of-test-user.com/",
                "https://github.com/Testuser", true, "https://res.cloudinary.com/Testuser/image/upload/Testuser/testimage1.jpg");
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
                        "appUserUsername": "TestUserUpdated",
                        "appUserAvatarUrl": "https://avatars.example.com/testUserUpdated",
                        "appUserGithubUrl": "https://github.com/testUserUpdated",
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
                        "appUserUsername": "TestUserUpdated",
                        "appUserAvatarUrl": "https://avatars.example.com/testUserUpdated",
                        "appUserGithubUrl": "https://github.com/testUserUpdated",
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
    void deleteRoom_shouldRemoveRoomFromRepository() throws Exception {
        // GIVEN

        OAuth2User mockOAuth2User = mock(OAuth2User.class);
        when(mockOAuth2User.getName()).thenReturn("123");  // The name of the authenticated user

        // Set the Mock OAuth2User in the SecurityContext and mark it as authenticated
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(mockOAuth2User, null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
        );

        RoomModel roomToDelete = new RoomModel("2", "Beethoven-Saal", "Beethovenstraße 1, 53115 Bonn",
                Category.ORCHESTER_HALL, "Ein moderner Saal für klassische Musik und Veranstaltungen.",
                "123", "Testuser", "https://avatars-of-test-user.com/",
                "https://github.com/Testuser", true, "https://res.cloudinary.com/Testuser/image/upload/Testuser/testimage2.jpg");
        roomRepository.save(roomToDelete);

        // Mock the Cloudinary API to simulate the deletion of the image
        Uploader mockUploader = mock(Uploader.class);
        when(mockUploader.destroy(any(), anyMap())).thenReturn(Map.of("result", "ok"));
        when(cloudinary.uploader()).thenReturn(mockUploader);

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/practice-hub/{roomId}", "2"))
                .andExpect(status().isNoContent());

        // THEN
        Assertions.assertFalse(roomRepository.existsById("2"));

        // Verifying that the Cloudinary destroy method was called with the correct image name
        verify(mockUploader).destroy(eq("testimage2"), anyMap());
    }

}
package ropold.backend.exception;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ropold.backend.model.Category;
import ropold.backend.model.RoomModelDto;
import ropold.backend.repository.RoomRepository;
import ropold.backend.service.AppUserService;
import static org.hamcrest.Matchers.is;


import java.util.Collections;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    RoomRepository roomRepository;

    @MockBean
    private Cloudinary cloudinary;

    @MockBean
    private AppUserService appUserService;

    @Test
    @WithMockUser(username = "user1") // Simuliert einen authentifizierten Benutzer
    void whenAccessDeniedExceptionThrown_thenReturnForbiddenResponse() throws Exception {
        // Simuliert, dass die Methode 'removeRoomFromFavorites' eine AccessDeniedException wirft

        OAuth2User mockOAuth2User = mock(OAuth2User.class);
        when(mockOAuth2User.getName()).thenReturn("123");  // The name of the authenticated user

        // Set the Mock OAuth2User in the SecurityContext and mark it as authenticated
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(mockOAuth2User, null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
        );

        doThrow(new AccessDeniedException("Access denied: User is not authorized to delete this room from favorites."))
                .when(appUserService).removeRoomFromFavorites("user1", "room1");

        // Simuliere eine DELETE-Anfrage, die die Ausnahme auslöst
        mockMvc.perform(delete("/api/practice-hub/favorites/user1/room1"))
                .andExpect(status().isForbidden()) // Überprüfe, ob der Status 403 (Forbidden) zurückgegeben wird
                .andExpect(jsonPath("$.message", is("Access denied: User is not authorized to delete this room from favorites."))) // Überprüfe, ob die Fehlermeldung korrekt ist
                .andReturn();
    }

    @Test
    void whenRoomNotFoundException_thenReturnsNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/practice-hub/{id}", "non-existing-id"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No Room found with id: non-existing-id"));
    }

    @Test
    @WithMockUser(username = "testUser")
    void postRoom_shouldFailValidation_whenAllFieldsExceptWishlistStatusAreInvalid() throws Exception {
        roomRepository.deleteAll();

        // Mock für den Uploader
        Uploader mockUploader = mock(Uploader.class);
        when(mockUploader.upload(any(), anyMap())).thenReturn(Map.of("secure_url", "https://res.cloudinary.com/dzjjlydk3/image/upload/v1733473109/dauqufxqzou7akwyoxha.jpg"));
        when(cloudinary.uploader()).thenReturn(mockUploader);

        // Wenn wir den Request durchführen, dann stellen wir sicher, dass ungültige Felder den richtigen Fehler auslösen
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/practice-hub")
                        .file(new MockMultipartFile("image", "test.jpg", "image/jpeg", "test image".getBytes()))
                        .file(new MockMultipartFile("roomModelDto", "","application/json", """
                    {
                        "name": "B",
                        "address": "Kreuzstr",
                        "category": "BAND_ROOM",
                        "description": "   ",
                        "appUserGithubId": "154427648",
                        "appUserUsername": "Ropold",
                        "appUserAvatarUrl": "https://avatars.githubusercontent.com/u/154427648?v=4",
                        "appUserGithubUrl": "https://github.com/Ropold",
                        "isActive": false,
                        "imageUrl": "https://res.cloudinary.com/dzjjlydk3/image/upload/v1733473109/dauqufxqzou9akwyoxha.jpg"
                    }
                    """.getBytes())))
                // Erwartete Validierungsfehler
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json("""
          {"address":"Address must contain at least two words, a 5-digit postal code, and a city name, e.g. 'Musterstraße 12345 Musterstadt'","name":"Name must contain at least 3 characters","description":"must not be blank"}
          """));
    }

    @Test
    @WithMockUser(username = "user123") // Simuliert einen authentifizierten Benutzer mit einer bestimmten ID
    void postRoom_shouldReturnAccessDenied_whenUserIsNotAuthorized() throws Exception {
        // GIVEN: Ein Raum, der von einem anderen Benutzer erstellt werden soll
        RoomModelDto roomModelDto = new RoomModelDto(
                "Room Name", "Room Address", Category.BAND_ROOM, "Room Description",
                "otherUserGithubId", "Other User", "https://avatars.githubusercontent.com/u/otherUser?v=4",
                "https://github.com/otherUser", true, "https://res.cloudinary.com/otherUser/image.jpg"
        );

        // Simuliere, dass der authentifizierte Benutzer nicht mit dem GitHub-Id im roomModelDto übereinstimmt
        OAuth2User mockOAuth2User = mock(OAuth2User.class);
        when(mockOAuth2User.getName()).thenReturn("user123"); // Authentifizierter Benutzer hat die ID 'user123'

        // Setzen des Mock OAuth2Users in den SecurityContext
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(mockOAuth2User, null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
        );

        // WHEN: POST-Anfrage wird mit einem RoomModelDto ausgeführt, bei dem die GitHub-ID nicht übereinstimmt
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/practice-hub")
                        .file(new MockMultipartFile("image", "test.jpg", "image/jpeg", "test image".getBytes()))
                        .file(new MockMultipartFile("roomModelDto", "", "application/json",
                                """
                                {
                                    "name": "Room Name",
                                    "address": "Room Address",
                                    "category": "BAND_ROOM",
                                    "description": "Room Description",
                                    "appUserGithubId": "otherUserGithubId",
                                    "appUserUsername": "Other User",
                                    "appUserAvatarUrl": "https://avatars.githubusercontent.com/u/otherUser?v=4",
                                    "appUserGithubUrl": "https://github.com/otherUser",
                                    "isActive": true,
                                    "imageUrl": "https://res.cloudinary.com/otherUser/image.jpg"
                                }
                                """.getBytes())))
                // THEN: Der Test erwartet einen 403 Forbidden Status und die AccessDeniedException sollte ausgelöst werden
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", is("Access denied: User is not authorized to create a room on behalf of another user.")))
                .andReturn();
    }

}
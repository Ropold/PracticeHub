package ropold.backend.exception;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ropold.backend.repository.RoomRepository;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;
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
}
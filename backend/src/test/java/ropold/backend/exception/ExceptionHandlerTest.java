package ropold.backend.exception;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ropold.backend.repository.RoomRepository;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    RoomRepository roomRepository;

    @Test
    void whenRoomNotFoundException_thenReturnsNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/practice-hub/{id}", "non-existing-id"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No Room found with id: non-existing-id"));
    }

    @Test
    void whenPostWithoutBody_thenReturnsInternalError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/practice-hub")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))  // empty Body
                .andExpect(status().isInternalServerError())  // expect 500 Statuscode
                .andExpect(jsonPath("$.message").value("Required request body is missing: public ropold.backend.model.RoomModel ropold.backend.controller.PracticeHubController.postRoom(ropold.backend.model.RoomModelDto)"));  // Erwartet die spezifische Fehlermeldung
    }

    @Test
    void whenPostWithInvalidData_thenReturnsInternalError() throws Exception {
        // GIVEN
        roomRepository.deleteAll();

        // Invalid Data
        String invalidRoomJson = """
             {
                "name": "",
                "address": "Neumarkt 1, 50667 Köln",
                "category": "Orchester-Saal",
                "descriptionINVALID": "Ein traditionsreicher Saal für Konzerte und Veranstaltungen.",
                "wishlistStatus": "INVALID_STATUS"
             }
            """;

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.post("/api/practice-hub")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRoomJson))
                .andExpect(status().isInternalServerError())  // expect 500 Statuscode
                .andExpect(jsonPath("$.message").exists())  // expect Error
                .andExpect(jsonPath("$.message").value(
                        "JSON parse error: Cannot deserialize value of type `ropold.backend.model.WishlistStatus` from String \"INVALID_STATUS\": not one of the values accepted for Enum class: [NOT_ON_WISHLIST, REMOVED_FROM_WISHLIST, ON_WISHLIST]"
                ));
    }

}

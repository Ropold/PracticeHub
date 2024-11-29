package ropold.backend.exception;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void whenRoomNotFoundException_thenReturnsNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/practice-hub/{id}", "non-existing-id"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No Room found with id: non-existing-id"));
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    void postRoom_shouldFailValidation_whenAllFieldsExceptWishlistStatusAreInvalid() throws Exception {
        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.post("/api/practice-hub")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                   "name": "hu",
                   "address": "Neumarkt Köln",
                   "category": "   ",
                   "description": "huhu",
                   "wishlistStatus": "ON_WISHLIST"
                }
                """)
                )
                // THEN
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json("""
                 {"address":"Address must contain at least a street name, a 5-digit postal code, and a city name, e.g. 'Musterstraße 12345 Musterstadt'",
                 "name":"size must be between 3 and 2147483647",
                 "category":"must not be blank"}
                """));
    }
}

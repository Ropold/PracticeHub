package ropold.backend.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
class MapBoxIntegrationsTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void getMapBoxy_expectMapBoxy() throws Exception {
        // WHEN
        mockMvc.perform(get("/api/mapbox/72c81498-f6b2-4a8a-911c-cd217a65e0da"))
                // THEN
                .andExpect(status().isOk())
                .andExpect(content().string("456")); // Erwartet den Wert aus application.properties
    }
}

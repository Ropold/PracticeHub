package ropold.backend.security;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;

@RestController
@RequestMapping("/api/mapbox")
class MapboxController {

    @Value("${mapbox.access.token}") // Setze den Mapbox-Token aus der application.properties oder .env
    private String mapboxAccessToken;

    @GetMapping("/access-token")
    public String getMapboxAccessToken() {
        return mapboxAccessToken; // Gibt den API-Key zurück
    }
}
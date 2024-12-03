package ropold.backend.security;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;

@RestController
@RequestMapping("/api/mapbox")
public class MapBoxController {

    @Value("${mapboxy}")
    private String mapboxy;

    @GetMapping("/72c81498-f6b2-4a8a-911c-cd217a65e0da")
    public String getMapBoxy() {
        return mapboxy;
    }
}

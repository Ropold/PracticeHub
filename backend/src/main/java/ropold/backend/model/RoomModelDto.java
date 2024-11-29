package ropold.backend.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RoomModelDto(
        @NotBlank
        @Size(min = 3)
        String name,

        @NotBlank
        @Pattern(
                regexp = "^.*?(\\b[A-Za-zäöüßÄÖÜ]+(?:straße|platz|weg|gasse|allee|ring|bahn|tor|damm|ufer)?\\b).*?\\b\\d{5}\\b.*?[A-Za-zäöüßÄÖÜ]+.*$",
                message = "Address must contain at least a street name, a 5-digit postal code, and a city name, e.g. 'Musterstraße 12345 Musterstadt'"
        )
        String address,

        @NotBlank
        @Size(min = 3)
        String category,

        @NotBlank
        @Size(min = 3)
        String description,
        WishlistStatus wishlistStatus
) {
}

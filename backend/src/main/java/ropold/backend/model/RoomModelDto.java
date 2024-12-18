package ropold.backend.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RoomModelDto(
        @NotBlank
        @Size(min = 3, message = "Name must contain at least 3 characters")
        String name,

        @NotBlank
        @Size(min = 11, message = "Address must contain at least two words, a 5-digit postal code, and a city name, e.g. 'Musterstraße 12345 Musterstadt'")
        String address,

        Category category,

        @NotBlank
        @Size(min = 3, message = "Description must contain at least 3 characters")
        String description,
        String appUserGithubId,
        String appUserUsername,
        String appUserAvatarUrl,
        String appUserGithubUrl,
        boolean isActive,
        String imageUrl
) {
}

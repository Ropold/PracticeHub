package ropold.backend.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RoomModelDto(
        @NotBlank
        @Size(min = 3)
        String name,
        @NotBlank
        @Size(min = 3)
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

package ropold.backend.model;

public record RoomModelDto(
        String name,
        String address,
        String category,
        String description,
        WishlistStatus wishlistStatus
) {
}

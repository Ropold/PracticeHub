package ropold.backend.model;

public record RoomModel(
        String id,
        String name,
        String address,
        String category,
        String description,
        WishlistStatus wishlistStatus, //weg
        String imageUrl
        //AppUserId appUserId String
) {
}

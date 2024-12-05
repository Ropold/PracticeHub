package ropold.backend.model;

public record RoomModel(
        String id,
        String name,
        String address,
        Category category,
        String description,
        String appUserGithubId,
        String imageUrl
) {
}

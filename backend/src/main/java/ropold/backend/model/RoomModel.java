package ropold.backend.model;

public record RoomModel(
        String id,
        String name,
        String address,
        String category,
        String description,
        String appUserGitbubId,
        String imageUrl
) {
}

package ropold.backend.model;

import java.util.List;

public record AppUser(
        String id,
        String username,
        String name,
        String avatarUrl,
        List<String> favorites
) {
}

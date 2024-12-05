package ropold.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ropold.backend.model.AppUser;
import ropold.backend.repository.AppUserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppUserService {

    private final AppUserRepository appUserRepository;

    public AppUser getUserById(String userId) {
        return appUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<String> getUserFavorites(String userId) {
        AppUser user = getUserById(userId);
        return user.favorites();
    }

    public void addRoomToFavorites(String userId, String roomId) {
        AppUser user = getUserById(userId);

        if (!user.favorites().contains(roomId)) {
            user.favorites().add(roomId);
            appUserRepository.save(user);
        }
    }

    public void removeRoomFromFavorites(String userId, String roomId) {
        AppUser user = getUserById(userId);

        if (user.favorites().contains(roomId)) {
            user.favorites().remove(roomId);
            appUserRepository.save(user);
        }
    }

}

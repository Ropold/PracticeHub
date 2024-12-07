package ropold.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ropold.backend.model.AppUser;
import ropold.backend.repository.AppUserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppUserServiceTest {

    @Mock
    private AppUserRepository appUserRepository;

    @InjectMocks
    private AppUserService appUserService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUserById_UserExists_ReturnsUser() {
        String userId = "user123";
        AppUser user = new AppUser(userId, "username", "name", "avatarUrl", "githubUrl", List.of());
        when(appUserRepository.findById(userId)).thenReturn(Optional.of(user));

        AppUser result = appUserService.getUserById(userId);

        assertNotNull(result);
        assertEquals(user, result);
        verify(appUserRepository, times(1)).findById(userId);
    }

    @Test
    void getUserById_UserDoesNotExist_ThrowsException() {
        String userId = "user123";
        when(appUserRepository.findById(userId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> appUserService.getUserById(userId));
        assertEquals("User not found", exception.getMessage());
        verify(appUserRepository, times(1)).findById(userId);
    }

    @Test
    void getUserFavorites_ReturnsFavorites() {
        String userId = "user123";
        List<String> favorites = List.of("room1", "room2");
        AppUser user = new AppUser(userId, "username", "name", "avatarUrl", "githubUrl", favorites);
        when(appUserRepository.findById(userId)).thenReturn(Optional.of(user));

        List<String> result = appUserService.getUserFavorites(userId);

        assertNotNull(result);
        assertEquals(favorites, result);
        verify(appUserRepository, times(1)).findById(userId);
    }

    @Test
    void addRoomToFavorites_RoomNotInFavorites_AddsRoom() {
        String userId = "user123";
        String roomId = "room1";
        List<String> favorites = new ArrayList<>();
        AppUser user = new AppUser(userId, "username", "name", "avatarUrl", "githubUrl", favorites);
        when(appUserRepository.findById(userId)).thenReturn(Optional.of(user));

        appUserService.addRoomToFavorites(userId, roomId);

        assertTrue(user.favorites().contains(roomId));
        verify(appUserRepository, times(1)).save(user);
    }

    @Test
    void addRoomToFavorites_RoomAlreadyInFavorites_DoesNothing() {
        String userId = "user123";
        String roomId = "room1";
        List<String> favorites = new ArrayList<>(List.of(roomId));
        AppUser user = new AppUser(userId, "username", "name", "avatarUrl", "githubUrl", favorites);
        when(appUserRepository.findById(userId)).thenReturn(Optional.of(user));

        appUserService.addRoomToFavorites(userId, roomId);

        assertEquals(1, user.favorites().size());
        verify(appUserRepository, never()).save(user);
    }

    @Test
    void removeRoomFromFavorites_RoomInFavorites_RemovesRoom() {
        String userId = "user123";
        String roomId = "room1";
        List<String> favorites = new ArrayList<>(List.of(roomId));
        AppUser user = new AppUser(userId, "username", "name", "avatarUrl", "githubUrl", favorites);
        when(appUserRepository.findById(userId)).thenReturn(Optional.of(user));

        appUserService.removeRoomFromFavorites(userId, roomId);

        assertFalse(user.favorites().contains(roomId));
        verify(appUserRepository, times(1)).save(user);
    }

    @Test
    void removeRoomFromFavorites_RoomNotInFavorites_DoesNothing() {
        String userId = "user123";
        String roomId = "room1";
        List<String> favorites = new ArrayList<>();
        AppUser user = new AppUser(userId, "username", "name", "avatarUrl", "githubUrl", favorites);
        when(appUserRepository.findById(userId)).thenReturn(Optional.of(user));

        appUserService.removeRoomFromFavorites(userId, roomId);

        assertTrue(user.favorites().isEmpty());
        verify(appUserRepository, never()).save(user);
    }
}
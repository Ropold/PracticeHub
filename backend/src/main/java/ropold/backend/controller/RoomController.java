package ropold.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ropold.backend.exception.AccessDeniedException;
import ropold.backend.model.RoomModel;
import ropold.backend.model.RoomModelDto;
import ropold.backend.service.AppUserService;
import ropold.backend.service.CloudinaryService;
import ropold.backend.service.RoomService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/practice-hub")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;
    private final CloudinaryService cloudinaryService;
    private final AppUserService appUserService;

    @GetMapping("/favorites")
    public List<RoomModel> getUserFavorites(@AuthenticationPrincipal OAuth2User authentication) {
        List<String> favoriteRoomIds = appUserService.getUserFavorites(authentication.getName());
        return roomService.getRoomsByIds(favoriteRoomIds);
    }

    @PostMapping("/favorites/{roomId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addRoomToFavorites(@PathVariable String roomId , @AuthenticationPrincipal OAuth2User authentication) {
        String authenticatedUserId = authentication.getName();
        appUserService.addRoomToFavorites(authenticatedUserId, roomId);
    }

    @DeleteMapping("/favorites/{roomId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeRoomFromFavorites(@PathVariable String roomId, @AuthenticationPrincipal OAuth2User authentication) {
        String authenticatedUserId = authentication.getName();

        appUserService.removeRoomFromFavorites(authenticatedUserId, roomId);
    }


    @GetMapping()
    public List<RoomModel> getAllRooms() {
        return roomService.getAllRooms();
    }

    @GetMapping("/active")
    public List<RoomModel> getActiveRooms() {
        return roomService.getActiveRooms();
    }

    @GetMapping("/{id}")
    public RoomModel getRoomById(@PathVariable String id) {
        return roomService.getRoomById(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public RoomModel postRoom(
            @RequestPart("roomModelDto") @Valid RoomModelDto roomModelDto,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal OAuth2User authentication) throws IOException {

        String authenticatedUserId = authentication.getName();
        if (!authenticatedUserId.equals(roomModelDto.appUserGithubId())) {
            throw new AccessDeniedException("Access denied: User is not authorized to create a room on behalf of another user.");
        }

        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            imageUrl = cloudinaryService.uploadImage(image);
        }

        return roomService.addRoom(
                new RoomModel(
                        null,
                        roomModelDto.name(),
                        roomModelDto.address(),
                        roomModelDto.category(),
                        roomModelDto.description(),
                        roomModelDto.appUserGithubId(),
                        roomModelDto.appUserUsername(),
                        roomModelDto.appUserAvatarUrl(),
                        roomModelDto.appUserGithubUrl(),
                        roomModelDto.isActive(),
                        imageUrl
                )
        );
    }

    @PutMapping("/{id}")
    public RoomModel putRoom(@PathVariable String id,
                             @RequestPart("roomModelDto") RoomModelDto roomModelDto,
                             @RequestPart(value = "image", required = false) MultipartFile image,
                             @AuthenticationPrincipal OAuth2User authentication) throws IOException {

        String authenticatedUserId = authentication.getName();
        RoomModel existingRoom = roomService.getRoomById(id);

        if (!authenticatedUserId.equals(existingRoom.appUserGithubId())) {
            throw new AccessDeniedException("Access denied: User is not authorized to update this room.");
        }

        String newImageUrl;
        if (image != null && !image.isEmpty()) {
            if (existingRoom.imageUrl() != null) {
                cloudinaryService.deleteImage(existingRoom.imageUrl());
            }
            newImageUrl = cloudinaryService.uploadImage(image);
        } else {
            newImageUrl = existingRoom.imageUrl();
        }

        RoomModel updatedRoom = new RoomModel(
                id,
                roomModelDto.name(),
                roomModelDto.address(),
                roomModelDto.category(),
                roomModelDto.description(),
                roomModelDto.appUserGithubId(),
                roomModelDto.appUserUsername(),
                roomModelDto.appUserAvatarUrl(),
                roomModelDto.appUserGithubUrl(),
                roomModelDto.isActive(),
                newImageUrl
        );

        return roomService.updateRoomWithPut(id, updatedRoom);
    }

    @PutMapping("/{id}/toggle-active")
    public RoomModel toggleActiveStatus(@PathVariable String id,@AuthenticationPrincipal OAuth2User authentication) {
        String authenticatedUserId = authentication.getName();
        RoomModel room = roomService.getRoomById(id);

        if (!authenticatedUserId.equals(room.appUserGithubId())) {
            throw new AccessDeniedException("Access denied: User is not authorized to toggle the active status of this room.");
        }
       return roomService.toggleActiveStatus(id);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteRoom(@PathVariable String id, @AuthenticationPrincipal OAuth2User authentication) {
        String authenticatedUserId = authentication.getName();

        RoomModel room = roomService.getRoomById(id);

        if (!authenticatedUserId.equals(room.appUserGithubId())) {
            throw new AccessDeniedException("Access denied: User is not authorized to delete this room.");
        }
        roomService.deleteRoom(id);
    }
}

package ropold.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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

    @GetMapping("/favorites/{userId}")
    public List<RoomModel> getUserFavorites(@PathVariable String userId) {
        List<String> favoriteRoomIds = appUserService.getUserFavorites(userId);
        return roomService.getRoomsByIds(favoriteRoomIds);
    }

    @PostMapping("/favorites/{userId}/{roomId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addRoomToFavorites(@PathVariable String userId, @PathVariable String roomId) {
        appUserService.addRoomToFavorites(userId, roomId);
    }

    @DeleteMapping("/favorites/{userId}/{roomId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeRoomFromFavorites(@PathVariable String userId, @PathVariable String roomId) {
        appUserService.removeRoomFromFavorites(userId, roomId);
    }

    @GetMapping()
    public List<RoomModel> getAllRooms() {
        return roomService.getAllRooms();
    }

    @GetMapping("/{id}")
    public RoomModel getRoomById(@PathVariable String id) {
        return roomService.getRoomById(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public RoomModel postRoom(
            @RequestPart("roomModelDto") @Valid RoomModelDto roomModelDto,
            @RequestPart("image") MultipartFile image) throws IOException {

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
                        roomModelDto.wishlistStatus(),
                        imageUrl
                )
        );
    }

    @PutMapping("/{id}")
    public RoomModel putRoom(@PathVariable String id,
                             @RequestPart("roomModelDto") RoomModelDto roomModelDto,
                             @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {

        RoomModel existingRoom = roomService.getRoomById(id);

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
                roomModelDto.wishlistStatus(),
                newImageUrl
        );
        return roomService.updateRoomWithPut(id, updatedRoom);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteRoom(@PathVariable String id) {
        roomService.deleteRoom(id);
    }
}

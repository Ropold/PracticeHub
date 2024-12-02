package ropold.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ropold.backend.model.RoomModel;
import ropold.backend.model.RoomModelDto;
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

        RoomModel existingRoom = roomService.getRoomById(id); // Hole den bestehenden Raum aus der DB

        // Wenn ein neues Bild hochgeladen wurde, löschen wir das alte Bild von Cloudinary
        String newImageUrl = null;
        if (image != null && !image.isEmpty()) {
            if (existingRoom.imageUrl() != null) {
                cloudinaryService.deleteImage(existingRoom.imageUrl()); // Altes Bild löschen
            }
            newImageUrl = cloudinaryService.uploadImage(image); // Neues Bild hochladen
        } else {
            // Wenn kein Bild hochgeladen wurde, bleibt das alte Bild
            newImageUrl = existingRoom.imageUrl();
        }

        // Aktualisiere den Raum mit den neuen Daten (einschließlich des neuen Bildes, falls vorhanden)
        RoomModel updatedRoom = new RoomModel(
                id,
                roomModelDto.name(),
                roomModelDto.address(),
                roomModelDto.category(),
                roomModelDto.description(),
                roomModelDto.wishlistStatus(),
                newImageUrl // Bild-URL wird entsprechend gesetzt
        );

        return roomService.updateRoomWithPut(id, updatedRoom);
    }


    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteRoom(@PathVariable String id) {
        roomService.deleteRoom(id);
    }
}

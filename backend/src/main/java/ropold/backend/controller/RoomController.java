package ropold.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
            @RequestPart(required = false) MultipartFile image,
            @RequestPart @Valid String json) throws IOException {

        // Parsing des JSON-Inputs
        RoomModelDto roomModelDto = new ObjectMapper().readValue(json, RoomModelDto.class);

        // Datei-Upload-Verarbeitung (falls vorhanden)
        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            imageUrl = cloudinaryService.uploadImage(image);
        }

        // Erstellung eines neuen RoomModel basierend auf DTO und hochgeladener Datei
        return roomService.addRoom(
                new RoomModel(
                        null,
                        roomModelDto.name(),
                        roomModelDto.address(),
                        roomModelDto.category(),
                        roomModelDto.description(),
                        roomModelDto.wishlistStatus(),
                        imageUrl // URL des hochgeladenen Bildes
                )
        );
    }

//    @ResponseStatus(HttpStatus.CREATED)
//    @PostMapping
//    public RoomModel postRoom(@RequestBody @Valid RoomModelDto roomModelDto) {
//        return roomService.addRoom(
//                new RoomModel(
//                        null,
//                        roomModelDto.name(),
//                        roomModelDto.address(),
//                        roomModelDto.category(),
//                        roomModelDto.description(),
//                        roomModelDto.wishlistStatus()
//                )
//        );
//    }

    @PutMapping("/{id}")
    public RoomModel putRoom(@PathVariable String id, @RequestBody RoomModelDto roomModelDto) {
        return roomService.updateRoomWithPut(id,
                new RoomModel(
                        id,
                        roomModelDto.name(),
                        roomModelDto.address(),
                        roomModelDto.category(),
                        roomModelDto.description(),
                        roomModelDto.wishlistStatus(),
                        roomModelDto.imageUrl()
                )
        );
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteRoom(@PathVariable String id) {
        roomService.deleteRoom(id);
    }
}

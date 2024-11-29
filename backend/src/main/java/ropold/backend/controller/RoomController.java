package ropold.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ropold.backend.model.RoomModel;
import ropold.backend.model.RoomModelDto;
import ropold.backend.service.RoomService;

import java.util.List;

@RestController
@RequestMapping("/api/practice-hub")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

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
    public RoomModel postRoom(@RequestBody @Valid RoomModelDto roomModelDto) {
        return roomService.addRoom(
                new RoomModel(
                        null,
                        roomModelDto.name(),
                        roomModelDto.address(),
                        roomModelDto.category(),
                        roomModelDto.description(),
                        roomModelDto.wishlistStatus()
                )
        );
    }

    @PutMapping("/{id}")
    public RoomModel putRoom(@PathVariable String id, @RequestBody RoomModelDto roomModelDto) {
        return roomService.updateRoomWithPut(id,
                new RoomModel(
                        id,
                        roomModelDto.name(),
                        roomModelDto.address(),
                        roomModelDto.category(),
                        roomModelDto.description(),
                        roomModelDto.wishlistStatus()
                )
        );
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteRoom(@PathVariable String id) {
        roomService.deleteRoom(id);
    }
}

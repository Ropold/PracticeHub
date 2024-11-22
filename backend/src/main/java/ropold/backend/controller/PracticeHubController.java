package ropold.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ropold.backend.exception.RoomErrorObject;
import ropold.backend.exception.RoomNotFoundException;
import ropold.backend.model.RoomModel;
import ropold.backend.model.RoomModelDto;
import ropold.backend.service.PracticeHubService;

import java.util.List;

@RestController
@RequestMapping("/api/practice-hub")
@RequiredArgsConstructor
public class PracticeHubController {

    private final PracticeHubService practiceHubService;

    @GetMapping()
    public List<RoomModel> getAllRooms() {
        return practiceHubService.getAllRooms();
    }

    @GetMapping("/{id}")
    public RoomModel getRoomById(@PathVariable String id) {
        return practiceHubService.getRoomById(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public RoomModel postRoom(@RequestBody RoomModelDto roomModelDto) {
        return practiceHubService.addRoom(
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
        return practiceHubService.updateRoomWithPut(id,
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

    @DeleteMapping("/{id}")
    public void deleteRoom(@PathVariable String id) {
        practiceHubService.deleteRoom(id);
    }
}
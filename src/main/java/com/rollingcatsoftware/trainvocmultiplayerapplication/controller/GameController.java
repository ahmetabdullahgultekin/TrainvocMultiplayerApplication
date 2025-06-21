package com.rollingcatsoftware.trainvocmultiplayerapplication.controller;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.Player;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.QuizSettings;
import com.rollingcatsoftware.trainvocmultiplayerapplication.service.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins = "*")
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    // Oda oluştur
    @PostMapping("/create")
    public ResponseEntity<GameRoom> createRoom(
            @RequestParam String hostName,
            @RequestParam(defaultValue = "true") boolean hostWantsToJoin,
            @RequestBody QuizSettings settings
    ) {
        GameRoom room = gameService.createRoom(hostName, settings, hostWantsToJoin);
        return ResponseEntity.ok(room);
    }

    // Join a room
    @PostMapping("/join")
    public ResponseEntity<?> joinRoom(@RequestParam String roomCode, @RequestParam String playerName) {
        Player player = gameService.joinRoom(roomCode, playerName);
        if (player == null) {
            // Return a more descriptive error message
            return ResponseEntity.badRequest().body(
                java.util.Collections.singletonMap("error", "Room not found or player could not be added. Please check the room code and player name.")
            );
        }
        return ResponseEntity.ok(player);
    }

    // Oda detayları
    @GetMapping("/{roomCode}")
    public ResponseEntity<GameRoom> getRoom(@PathVariable String roomCode) {
        GameRoom room = gameService.getRoom(roomCode);
        if (room == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(room);
    }

    // List all rooms
    @GetMapping("/rooms")
    public List<GameRoom> getAllRooms() {
        return gameService.getAllRooms();
    }
}

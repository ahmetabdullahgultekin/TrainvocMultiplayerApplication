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
            @RequestParam(required = false) String avatarId,
            @RequestParam(defaultValue = "true") boolean hostWantsToJoin,
            @RequestParam(required = false) String hashedPassword,
            @RequestBody QuizSettings settings
    ) {
        Integer avatarIndex = null;
        if (avatarId != null) {
            try {
                avatarIndex = Integer.valueOf(avatarId);
            } catch (NumberFormatException e) {
                avatarIndex = null;
            }
        }
        GameRoom room = gameService.createRoom(hostName, avatarIndex, settings, hostWantsToJoin, hashedPassword);
        return ResponseEntity.ok(room);
    }

    // Join a room (şifre kontrolü eklendi)
    @PostMapping("/join")
    public ResponseEntity<?> joinRoom(@RequestParam String roomCode, @RequestParam String playerName, @RequestParam(required = false) String avatarId, @RequestParam(required = false) String hashedPassword) {
        Integer avatarIndex = null;
        if (avatarId != null) {
            try {
                avatarIndex = Integer.valueOf(avatarId);
            } catch (NumberFormatException _) {
            }
        }
        boolean passwordOk = gameService.checkRoomPassword(roomCode, hashedPassword);
        if (!passwordOk) {
            return ResponseEntity.status(403).body(java.util.Collections.singletonMap("error", "Room password is incorrect."));
        }
        Player player = gameService.joinRoom(roomCode, playerName, avatarIndex);
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

    // Odayı başlat (şifre kontrolü eklendi)
    @PostMapping("/rooms/{roomCode}/start")
    public ResponseEntity<?> startRoom(@PathVariable String roomCode, @RequestParam(required = false) String hashedPassword) {
        boolean passwordOk = gameService.checkRoomPassword(roomCode, hashedPassword);
        if (!passwordOk) {
            return ResponseEntity.status(403).body(java.util.Collections.singletonMap("error", "Room password is incorrect."));
        }
        boolean started = gameService.startRoom(roomCode);
        if (started) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Odayı dağıt (şifre kontrolü eklendi)
    @PostMapping("/rooms/{roomCode}/disband")
    public ResponseEntity<?> disbandRoom(@PathVariable String roomCode, @RequestParam(required = false) String hashedPassword) {
        boolean passwordOk = gameService.checkRoomPassword(roomCode, hashedPassword);
        if (!passwordOk) {
            return ResponseEntity.status(403).body(java.util.Collections.singletonMap("error", "Room password is incorrect."));
        }
        boolean deleted = gameService.disbandRoom(roomCode);
        if (deleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Oyuncu lobiden ayrılır
    @PostMapping("/rooms/{roomCode}/leave")
    public ResponseEntity<?> leaveRoom(@PathVariable String roomCode, @RequestParam String playerId) {
        boolean removed = gameService.leaveRoom(roomCode, playerId);
        if (removed) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

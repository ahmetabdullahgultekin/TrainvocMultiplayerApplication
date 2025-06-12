package com.rollingcatsoftware.trainvocmultiplayerapplication.controller;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.Player;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.QuizSettings;
import com.rollingcatsoftware.trainvocmultiplayerapplication.service.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // Odaya katıl
    @PostMapping("/join")
    public ResponseEntity<Player> joinRoom(@RequestParam String roomCode, @RequestParam String playerName) {
        Player player = gameService.joinRoom(roomCode, playerName);
        if (player == null) {
            return ResponseEntity.badRequest().build();
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
}


package com.rollingcatsoftware.trainvocmultiplayerapplication.controller;

import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.AnswerRequest;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.Player;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.QuizSettings;
import com.rollingcatsoftware.trainvocmultiplayerapplication.repository.PlayerRepository;
import com.rollingcatsoftware.trainvocmultiplayerapplication.service.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/game")
public class GameController {
    private final GameService gameService;
    private final PlayerRepository playerRepo;

    public GameController(GameService gameService, PlayerRepository playerRepo) {
        this.gameService = gameService;
        this.playerRepo = playerRepo;
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
            } catch (NumberFormatException _) {
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

    // Oda oyuncularını getir
    @GetMapping("/players")
    public ResponseEntity<List<Player>> getPlayers(@RequestParam String roomCode) {
        List<Player> players = gameService.getPlayersByRoomCode(roomCode);
        if (players == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(players);
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

    @PostMapping("/answer")
    public ResponseEntity<?> submitAnswer(@RequestBody AnswerRequest answerRequest) {
        var room = gameService.getRoom(answerRequest.getRoomCode());
        if (room == null) {
            return ResponseEntity.status(404).body(java.util.Collections.singletonMap("error", "Oda bulunamadı."));
        }
        List<Player> players = room.getPlayers();
        Player player = players.stream()
                .filter(p -> p.getId().equals(answerRequest.getPlayerId()))
                .findFirst().orElse(null);
        System.out.println("[DEBUG] Bulunan Player: " + (player != null ? player.getName() : "null"));
        if (player == null) {
            return ResponseEntity.status(404).body(java.util.Collections.singletonMap("error", "Oyuncu bulunamadı."));
        }
        // Skoru backend'de hesapla (oda ayarlarından maxTime alınır)
        int maxTime = room.getQuestionDuration();
        System.out.println("[SCORE] isCorrect: " + answerRequest.isCorrect() + ", answerTime: " + answerRequest.getAnswerTime() + ", optionPickRate: " + answerRequest.getOptionPickRate() + ", maxTime: " + maxTime);
        int calculatedScore = AnswerRequest.calculateScore(
                answerRequest.isCorrect(),
                answerRequest.getAnswerTime(),
                answerRequest.getOptionPickRate(),
                maxTime
        );
        System.out.println("[SCORE] Calculated score: " + calculatedScore);
        player.setScore(player.getScore() + calculatedScore);
        playerRepo.save(player);
        // Güncel oyuncu listesini veritabanından çekerek dön
        List<Player> updatedPlayers = playerRepo.findByRoom(room);
        return ResponseEntity.ok(java.util.Collections.singletonMap("players", updatedPlayers));
    }
}

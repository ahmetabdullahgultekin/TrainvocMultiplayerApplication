package com.rollingcatsoftware.trainvocmultiplayerapplication.service;

import com.rollingcatsoftware.trainvocmultiplayerapplication.exception.RoomPasswordException;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.Player;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.QuizSettings;
import com.rollingcatsoftware.trainvocmultiplayerapplication.repository.GameRoomRepository;
import com.rollingcatsoftware.trainvocmultiplayerapplication.repository.PlayerRepository;
import com.rollingcatsoftware.trainvocmultiplayerapplication.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class GameService {
    // Yeni avatar dizisi: emoji listesi
    private static final String[] ALLOWED_AVATARS = {
            "🦊", "🐱", "🐶", "🐵", "🐸", "🐼", "🐧", "🐯", "🦁", "🐮",
            "🐨", "🐰", "🐻", "🐷", "🐔", "🦄", "🐙", "🐢", "🐳", "🐝"
    };
    private final GameRoomRepository gameRoomRepo;
    private final PlayerRepository playerRepo;

    public GameService(GameRoomRepository gameRoomRepo, PlayerRepository playerRepo, QuestionRepository questionRepo) {
        this.gameRoomRepo = gameRoomRepo;
        this.playerRepo = playerRepo;
    }

    private int getRandomAvatarId() {
        return (int) (Math.random() * ALLOWED_AVATARS.length);
    }

    private boolean isValidAvatarId(Integer avatarId) {
        return avatarId != null && avatarId >= 0 && avatarId < ALLOWED_AVATARS.length;
    }

    private Player createPlayer(GameRoom room, String name, Integer avatarId) {
        Player player = new Player();
        player.setId(UUID.randomUUID().toString());
        player.setRoom(room);
        player.setName(name);
        player.setScore(0);
        player.setCorrectCount(0);
        player.setWrongCount(0);
        player.setTotalAnswerTime(0);
        if (isValidAvatarId(avatarId)) {
            player.setAvatarId(avatarId);
        } else {
            player.setAvatarId(getRandomAvatarId());
        }
        return player;
    }

    // Timing-safe equals
    private boolean timingSafeEquals(String a, String b) {
        if (a == null || b == null) return false;
        if (a.length() != b.length()) return false;
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }

    public GameRoom createRoom(String hostName, Integer avatarId, QuizSettings settings, boolean hostWantsToJoin, String hashedPassword) {
        GameRoom room = new GameRoom();
        room.setRoomCode(UUID.randomUUID().toString().substring(0, 5).toUpperCase());
        room.setCurrentQuestionIndex(0);
        room.setStarted(false);
        room.setQuestionDuration(settings.getQuestionDuration());
        room.setOptionCount(settings.getOptionCount());
        room.setLevel(settings.getLevel());
        room.setTotalQuestionCount(settings.getTotalQuestionCount());
        room.setLastUsed(LocalDateTime.now());
        room.setHashedPassword(hashedPassword);
        room = gameRoomRepo.save(room);
        Player host = createPlayer(room, hostName, avatarId);
        playerRepo.save(host);
        room.getPlayers().clear();
        if (hostWantsToJoin) {
            room.getPlayers().add(host);
        }
        room.setHostId(host.getId());
        gameRoomRepo.save(room);
        return room;
    }

    public Player joinRoom(String roomCode, String playerName, Integer avatarId) {
        GameRoom room = gameRoomRepo.findById(roomCode).orElse(null);
        if (room == null) return null;
        Player player = createPlayer(room, playerName, avatarId);
        return playerRepo.save(player);
    }

    public GameRoom getRoom(String roomCode) {
        updateLastUsed(roomCode);
        return gameRoomRepo.findById(roomCode).orElse(null);
    }

    public void saveRoom(GameRoom room) {
        gameRoomRepo.save(room);
    }

    public List<GameRoom> getAllRooms() {
        return gameRoomRepo.findAll();
    }

    public void updateLastUsed(String roomCode) {
        GameRoom room = gameRoomRepo.findByRoomCode(roomCode);
        if (room != null) {
            room.setLastUsed(LocalDateTime.now());
            gameRoomRepo.save(room);
        }
    }

    public boolean startRoom(String roomCode) {
        GameRoom room = gameRoomRepo.findByRoomCode(roomCode);
        if (room != null) {
            room.setStarted(true);
            gameRoomRepo.save(room);
            return true;
        }
        return false;
    }

    public boolean disbandRoom(String roomCode) {
        GameRoom room = gameRoomRepo.findByRoomCode(roomCode);
        if (room != null) {
            gameRoomRepo.delete(room);
            return true;
        }
        return false;
    }

    public boolean leaveRoom(String roomCode, String playerId) {
        Player player = playerRepo.findById(playerId).orElse(null);
        if (player != null && player.getRoom() != null && player.getRoom().getRoomCode().equals(roomCode)) {
            playerRepo.delete(player);
            return true;
        }
        return false;
    }

    public boolean checkRoomPassword(String roomCode, String hashedPassword) {
        GameRoom room = gameRoomRepo.findByRoomCode(roomCode);
        if (room == null) return false;
        String roomHash = room.getHashedPassword();
        if (roomHash == null || roomHash.isEmpty()) return true; // Şifresiz oda
        return timingSafeEquals(roomHash, hashedPassword);
    }

    public void throwIfRoomPasswordInvalid(String roomCode, String hashedPassword) {
        GameRoom room = gameRoomRepo.findByRoomCode(roomCode);
        if (room == null) throw new RoomPasswordException("RoomNotFound", "Oda bulunamadı.");
        String roomHash = room.getHashedPassword();
        if (roomHash == null || roomHash.isEmpty()) return; // Şifresiz oda
        if (hashedPassword == null || hashedPassword.isEmpty())
            throw new RoomPasswordException("RoomPasswordRequired", "Bu oda için şifre gereklidir.");
        if (!timingSafeEquals(roomHash, hashedPassword))
            throw new RoomPasswordException("InvalidRoomPassword", "Şifre yanlış.");
    }

    public List<Player> getPlayersByRoomCode(String roomCode) {
        GameRoom room = gameRoomRepo.findByRoomCode(roomCode);
        if (room == null) return null;
        return playerRepo.findByRoom(room);
    }
}

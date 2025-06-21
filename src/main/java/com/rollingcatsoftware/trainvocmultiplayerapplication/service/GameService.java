package com.rollingcatsoftware.trainvocmultiplayerapplication.service;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.Player;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.QuizSettings;
import com.rollingcatsoftware.trainvocmultiplayerapplication.repository.GameRoomRepository;
import com.rollingcatsoftware.trainvocmultiplayerapplication.repository.PlayerRepository;
import com.rollingcatsoftware.trainvocmultiplayerapplication.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GameService {
    private final GameRoomRepository gameRoomRepo;
    private final PlayerRepository playerRepo;

    public GameService(GameRoomRepository gameRoomRepo, PlayerRepository playerRepo, QuestionRepository questionRepo) {
        this.gameRoomRepo = gameRoomRepo;
        this.playerRepo = playerRepo;
    }

    private Player createPlayer(GameRoom room, String name) {
        Player player = new Player();
        player.setId(UUID.randomUUID().toString());
        player.setRoom(room);
        player.setName(name);
        player.setScore(0);
        player.setCorrectCount(0);
        player.setWrongCount(0);
        player.setTotalAnswerTime(0);
        return player;
    }

    public GameRoom createRoom(String hostName, QuizSettings settings, boolean hostWantsToJoin) {
        GameRoom room = new GameRoom();
        room.setRoomCode(UUID.randomUUID().toString().substring(0, 5).toUpperCase());
        room.setCurrentQuestionIndex(0);
        room.setStarted(false);
        room.setQuestionDuration(settings.getQuestionDuration());
        room.setOptionCount(settings.getOptionCount());
        room.setLevel(settings.getLevel());
        room.setTotalQuestionCount(settings.getTotalQuestionCount());

        // (DEMO için soruları burada üretebilir ya da ayrı bir yerden çekebilirsin.)
        // room.setQuestions(...);

        room = gameRoomRepo.save(room);
        Player host = createPlayer(room, hostName);
        playerRepo.save(host);
        room.getPlayers().clear();
        if (hostWantsToJoin) {
            room.getPlayers().add(host);
        }
        room.setHostId(host.getId());
        gameRoomRepo.save(room);
        return room;
    }

    public Player joinRoom(String roomCode, String playerName) {
        GameRoom room = gameRoomRepo.findById(roomCode).orElse(null);
        if (room == null) return null;
        Player player = createPlayer(room, playerName);
        return playerRepo.save(player);
    }

    public GameRoom getRoom(String roomCode) {
        return gameRoomRepo.findById(roomCode).orElse(null);
    }

    public void saveRoom(GameRoom room) {
        gameRoomRepo.save(room);
    }

    public List<GameRoom> getAllRooms() {
        return gameRoomRepo.findAll();
    }
}

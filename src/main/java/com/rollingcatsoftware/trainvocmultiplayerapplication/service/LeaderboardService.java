package com.rollingcatsoftware.trainvocmultiplayerapplication.service;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.Player;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaderboardService {
    public List<Player> getSortedPlayers(GameRoom room) {
        return room.getPlayers().stream()
                .sorted(Comparator.comparingInt(Player::getScore).reversed())
                .collect(Collectors.toList());
    }

    public List<Player> getTop3Players(GameRoom room) {
        return getSortedPlayers(room).stream().limit(3).collect(Collectors.toList());
    }
}

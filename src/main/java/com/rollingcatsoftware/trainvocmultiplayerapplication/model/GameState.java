package com.rollingcatsoftware.trainvocmultiplayerapplication.model;

import lombok.Data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
public class GameState {
    private Map<String, Answer> latestAnswers = new HashMap<>();
    private Set<String> answeredPlayers = new HashSet<>();
}
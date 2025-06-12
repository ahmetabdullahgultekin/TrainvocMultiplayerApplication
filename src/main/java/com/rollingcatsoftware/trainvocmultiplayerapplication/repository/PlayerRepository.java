package com.rollingcatsoftware.trainvocmultiplayerapplication.repository;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, String> {
}


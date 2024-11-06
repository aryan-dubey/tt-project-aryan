package com.auriga_tt.repository;

import com.auriga_tt.model.GameFoul;
import com.auriga_tt.model.MatchGame;
import com.auriga_tt.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameFoulRepository extends JpaRepository<GameFoul, Long> {
    List<GameFoul> findByMatchGame(MatchGame matchGame);
    List<GameFoul> findByPlayer(Player player);
}

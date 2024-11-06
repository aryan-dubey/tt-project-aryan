package com.auriga_tt.repository;

import com.auriga_tt.model.Match;
import com.auriga_tt.model.MatchGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchGameRepository extends JpaRepository<MatchGame, Long> {
    List<MatchGame> findByMatch(Match match);
}
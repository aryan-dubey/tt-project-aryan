package com.auriga_tt.repository;

import com.auriga_tt.model.Match;
import com.auriga_tt.model.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByTournament(Tournament tournament);
    List<Match> findByStatus(Match.MatchStatus status);
}
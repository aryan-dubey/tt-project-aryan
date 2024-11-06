package com.auriga_tt.repository;

import com.auriga_tt.model.Player;
import com.auriga_tt.model.Team;
import com.auriga_tt.model.Tournament;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    Page<Team> findByTournament(Tournament tournament, Pageable pageable);
    List<Team> findByTournament(Tournament tournament);
    List<Team> findByPlayer1OrPlayer2(Player player1, Player player2);
}

package com.auriga_tt.repository;

import com.auriga_tt.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TournamentParticipantRepository extends JpaRepository<TournamentParticipant, Long> {
    List<TournamentParticipant> findByTournament(Tournament tournament);
    List<TournamentParticipant> findByPlayer(Player player);
    List<TournamentParticipant> findByTeam(Team team);
    Page<TournamentParticipant> findAll(Specification<TournamentParticipant> spec, Pageable pageable);
}

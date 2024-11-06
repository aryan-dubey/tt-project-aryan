package com.auriga_tt.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

//TournamentParticipant is the one entity which carries the entrance of a player to a tournament
//If the tournament is singles, the team_id would be left null and a player_id from the players table would be assigned
//If the tournament is doubles, the player_id would be left null and team id from the teams table would be assigned

@Entity
@Table(name = "tournament_participant")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TournamentParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long participantId;

    @ManyToOne
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @Enumerated(EnumType.STRING)
    private ParticipantStatus status;

    private LocalDateTime createdAt;

    public enum ParticipantStatus {
        REGISTERED, ACTIVE, WITHDRAWN, ELIMINATED
    }
}
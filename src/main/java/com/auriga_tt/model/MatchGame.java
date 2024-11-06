package com.auriga_tt.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "match_game")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchGame {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long matchGameId;

    @ManyToOne
    @JoinColumn(name = "match_id")
    private Match match;

    private Integer gameNumber;
    private Integer participant1Score;
    private Integer participant2Score;

    @ManyToOne
    @JoinColumn(name = "winner_participant_id")
    private TournamentParticipant winnerParticipant;

    private LocalDateTime createdAt;
}

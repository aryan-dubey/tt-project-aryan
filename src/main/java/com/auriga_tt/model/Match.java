package com.auriga_tt.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "match")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long matchId;

    @ManyToOne
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    private Integer roundNumber;

    @ManyToOne
    @JoinColumn(name = "participant1_id")
    private TournamentParticipant participant1;

    @ManyToOne
    @JoinColumn(name = "participant2_id")
    private TournamentParticipant participant2;

    @ManyToOne
    @JoinColumn(name = "winner_participant_id")
    private TournamentParticipant winnerParticipant;

    private LocalDateTime scheduledTime;

    @Enumerated(EnumType.STRING)
    private MatchStatus status;

    @ManyToOne
    @JoinColumn(name = "recorded_by")
    private User recordedBy;

    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    public enum MatchStatus {
        SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED
    }
}

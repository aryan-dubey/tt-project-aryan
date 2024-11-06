package com.auriga_tt.dto;

import com.auriga_tt.model.Match;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchDTO {
    private Long matchId;
    private TournamentDTO tournament;
    private Integer roundNumber;
    private TournamentParticipantDTO participant1;
    private TournamentParticipantDTO participant2;
    private TournamentParticipantDTO winnerParticipant;
    private LocalDateTime scheduledTime;
    private Match.MatchStatus status;
    private UserDTO recordedBy;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}

package com.auriga_tt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchGameDTO {
    private Long matchGameId;
    private MatchDTO match;
    private Integer gameNumber;
    private Integer participant1Score;
    private Integer participant2Score;
    private TournamentParticipantDTO winnerParticipant;
    private LocalDateTime createdAt;
}

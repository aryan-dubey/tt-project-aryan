package com.auriga_tt.dto;

import com.auriga_tt.model.TournamentParticipant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TournamentParticipantDTO {
    private Long participantId;
    private Long tournamentId;    // Changed from TournamentDTO
    private Long playerId;        // Changed from PlayerDTO
    private Long teamId;          // Changed from TeamDTO
    private TournamentParticipant.ParticipantStatus status;
    private LocalDateTime createdAt;
    private TournamentDTO tournament;
    private PlayerDTO player;
    private TeamDTO team;
}
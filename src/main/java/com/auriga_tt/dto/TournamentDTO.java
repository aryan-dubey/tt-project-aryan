package com.auriga_tt.dto;

import com.auriga_tt.model.Tournament;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TournamentDTO {
    private Long tournamentId;
    private String name;
    private Tournament.TournamentType tournamentType;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer maxParticipants;
    private Tournament.TournamentStatus status;
    private String rules;
    private UserDTO createdBy;
    private LocalDateTime createdAt;
}
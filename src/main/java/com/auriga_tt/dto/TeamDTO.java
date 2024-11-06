package com.auriga_tt.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamDTO {
    private Long teamId;
    private Long tournamentId;
    private Long player1Id;
    private Long player2Id;
    private PlayerDTO player1;
    private PlayerDTO player2;
    @NotBlank(message = "Team name cannot be empty")
    private String teamName;
    private LocalDateTime createdAt;
}

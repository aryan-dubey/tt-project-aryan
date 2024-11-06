package com.auriga_tt.dto;

import com.auriga_tt.model.GameFoul;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameFoulDTO {
    private Long foulId;
    private MatchGameDTO matchGame;
    private PlayerDTO player;
    private GameFoul.FoulType foulType;
    private String description;
    private Integer penaltyPoints;
    private LocalDateTime createdAt;
}
package com.auriga_tt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerDTO {
    private Long playerId;
    private Long userId;
    private UserDTO user;
    private Integer skillLevel;
    private LocalDateTime createdAt;
}

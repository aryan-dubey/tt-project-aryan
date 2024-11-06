package com.auriga_tt.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

//Player is created from a user i.e. a player will always be a user but a user can or cannot be a player

@Entity
@Table(name = "player")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long playerId;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Integer skillLevel;
    private LocalDateTime createdAt;
}

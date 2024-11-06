package com.auriga_tt.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "game_foul")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameFoul {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long foulId;

    @ManyToOne
    @JoinColumn(name = "match_game_id")
    private MatchGame matchGame;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    @Enumerated(EnumType.STRING)
    private FoulType foulType;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer penaltyPoints;
    private LocalDateTime createdAt;

    public enum FoulType {
        SERVICE_FAULT, EDGE_BALL, DOUBLE_HIT, TIME_VIOLATION, UNSPORTSMANLIKE_CONDUCT
    }
}
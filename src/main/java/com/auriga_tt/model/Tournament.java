package com.auriga_tt.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

//Tournament can be of two types singles or doubles

@Entity
@Table(name = "tournament")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tournament {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tournamentId;

    private String name;

    @Enumerated(EnumType.STRING)
    private TournamentType tournamentType;

    private LocalDate startDate;
    private LocalDate endDate;
    private Integer maxParticipants;

    @Enumerated(EnumType.STRING)
    private TournamentStatus status;

    @Column(columnDefinition = "TEXT")
    private String rules;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    private LocalDateTime createdAt;

    public enum TournamentType {
        SINGLES, DOUBLES
    }

    public enum TournamentStatus {
        UPCOMING, ONGOING, COMPLETED, CANCELLED
    }
}
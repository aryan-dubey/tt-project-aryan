package com.auriga_tt.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_contact_method")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserContactMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contactId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private ContactType contactType;

    private String contactValue;
    private Boolean isVerified;
    private Boolean isPrimary;
    private String verificationToken;
    private LocalDateTime verificationExpiry;
    private LocalDateTime createdAt;

    public enum ContactType {
        EMAIL, PHONE, OTHER
    }
}
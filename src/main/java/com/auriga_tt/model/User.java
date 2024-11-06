package com.auriga_tt.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

//A user is created here, it can be a user or admin

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    private String fullName;
    private String department;
    private String username;
    private String passwordHash;
    private String role;
    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    private Integer loginRetries;

    @Enumerated(EnumType.STRING)
    private UserStatus isActive;

    private String oAuth2Provider;

    private Boolean isDeleted;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<UserContactMethod> contactMethods;

    public enum UserStatus {
        ACTIVE, INACTIVE, BANNED
    }

    // Add this method to get the primary email
    public String getEmail() {
        return this.contactMethods.stream()
                .filter(method -> method.getContactType() == UserContactMethod.ContactType.EMAIL && method.getIsPrimary())
                .findFirst()
                .map(UserContactMethod::getContactValue)
                .orElse(null);
    }
}
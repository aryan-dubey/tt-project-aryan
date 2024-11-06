package com.auriga_tt.dto;

import com.auriga_tt.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long userId;
    private String fullName;
    private String department;
    private String username;
    private String role;
    private String profileImage;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private User.UserStatus isActive;
    private List<UserContactMethodDTO> contactMethods;

    public UserDTO(Long userId, String username) {
        this.userId = userId;
        this.username = username;
    }
}

package com.auriga_tt.dto;

import com.auriga_tt.model.Notification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long notificationId;
    private UserDTO user;
    private String message;
    private Notification.NotificationStatus status;
    private LocalDateTime createdAt;
}

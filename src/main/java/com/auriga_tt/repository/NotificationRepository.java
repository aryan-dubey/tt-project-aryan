package com.auriga_tt.repository;

import com.auriga_tt.model.Notification;
import com.auriga_tt.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUser(User user);
    List<Notification> findByUserAndStatus(User user, Notification.NotificationStatus status);
}

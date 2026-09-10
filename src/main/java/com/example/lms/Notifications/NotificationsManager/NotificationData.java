package com.example.lms.Notifications.NotificationsManager;

import com.example.lms.common.enums.UserRole;
import com.example.lms.Notifications.Enums.NotificationType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

@Entity
@Table(name = "notification_data")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationData {
    @Id
    @Column(name = "id", updatable = false, nullable = false, unique = true, length = 36)
    private String notificationDataID;

    @Column(nullable = false)
    private NotificationType notificationType;

    @Column(nullable = false)
    private UserRole receiverType;

    @Column(nullable = false)
    private String receiverID;

    @Column(nullable = false)
    private String message;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(nullable = false)
    private LocalDateTime createdAt;

    private static final AtomicLong counter = new AtomicLong();

    public NotificationData(NotificationType notificationType, UserRole receiverType, String receiverID, String message, LocalDateTime createdAt) {
        this.notificationDataID = String.valueOf(counter.incrementAndGet());
        this.notificationType = notificationType;
        this.receiverType = receiverType;
        this.receiverID = receiverID;
        this.message = message;
        this.createdAt = createdAt;
    }

}

package com.example.lms.Notifications.NotificationsManager;

import jakarta.persistence.*;
import lombok.*;

import java.util.concurrent.atomic.AtomicLong;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    @Column(name = "id", updatable = false, nullable = false, unique = true, length = 36)
    private String notificationID;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "notification_data_id", referencedColumnName = "id")
    private NotificationData notificationData;

    @Column(nullable = false)
    String createdAt_formatted;

    @Column(nullable = false)
    boolean isRead;


    private static final AtomicLong counter = new AtomicLong();


    public Notification(NotificationData notificationData, String dateFormatted, boolean isRead) {
        this.notificationID = String.valueOf(counter.incrementAndGet());
        this.notificationData = notificationData;
        this.createdAt_formatted = dateFormatted;
        this.isRead = isRead;
    }

}

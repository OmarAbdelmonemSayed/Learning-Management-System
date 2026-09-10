package com.example.lms.Notifications.NotificationsManager;

import com.example.lms.common.enums.UserRole;
import com.example.lms.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailNotificationSender emailNotificationSender;
    private final UserRepository userRepository;


    private String formatDate(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE d/M/yyyy hh:mm a");
        return date.format(formatter);
    }

    public Notification addNotification(NotificationData notificationData) {
        String date_formatted = formatDate(notificationData.getCreatedAt());
        Notification notification = new Notification(notificationData, date_formatted, false);
        notificationRepository.save(notification);
        return notification;
    }

    public List <Notification> getNotifications(String receiverID, boolean isUnreadOnly) {
        //didn't use the return of notificationRepository.retreiveNotificationsForUser directly, because it return an immutable list, which throws an exception when sorted. To make it mutable, we first need to explicitly put it in an ArrayList
        List<Notification> userNotifications = new ArrayList<>(notificationRepository.retreiveNotificationsForUser(receiverID, isUnreadOnly));
        if (userNotifications.size() > 1)
        {
            userNotifications = userNotifications.stream()
                    .sorted((notification1, notification2) ->
                            notification2.getNotificationData().getCreatedAt()
                                    .compareTo(notification1.getNotificationData().getCreatedAt())
                    )
                    .collect(Collectors.toList());
        }
        return userNotifications;
    }

    public void markNotificationAsRead(String notificationID) {
        Notification target = notificationRepository.findById(notificationID).orElse(null);
        target.setRead(true);
        notificationRepository.save(target);
    }

    public void sendNotificationByEmail (Notification notification) throws IOException, InterruptedException {
        String subject = "You have a new notification from the LMS";
        String receiverEmailAddress = userRepository.findById(notification.getNotificationData().getReceiverID()).orElse(null).getEmail();
        emailNotificationSender.sendEmail(receiverEmailAddress, subject, notification);
    }

}

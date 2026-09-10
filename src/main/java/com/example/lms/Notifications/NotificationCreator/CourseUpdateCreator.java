package com.example.lms.Notifications.NotificationCreator;

import com.example.lms.Notifications.NotificationsManager.NotificationData;
import com.example.lms.Notifications.Enums.NotificationType;
import com.example.lms.common.enums.UserRole;

public class CourseUpdateCreator extends NotificationCreator {

    public CourseUpdateCreator(String receiverID, String courseName) {
        super(receiverID, courseName);
    }

    public NotificationData createCourseUpdateNotification() {
        String message = "There's a new item in " + courseName + " feed\n";
        return new NotificationData(NotificationType.COURSE_UPDATE, UserRole.STUDENT, receiverID, message, createdAt);
    }
}

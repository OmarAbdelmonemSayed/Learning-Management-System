package com.example.lms.Notifications.NotificationCreator;

import com.example.lms.common.enums.UserRole;
import com.example.lms.Notifications.NotificationsManager.NotificationData;
import com.example.lms.Notifications.Enums.NotificationType;

public class AssignmentGradedCreator extends NotificationCreator {

    private String assignmentName;

    public AssignmentGradedCreator(String receiverID, String courseName, String assignmentName) {
        super(receiverID, courseName);
        this.assignmentName = assignmentName;
    }

    public NotificationData createAssignmentGradedNotification() {
        String message = "The grade of " + assignmentName + " in " + courseName + " course is out now\n";
        return new NotificationData(NotificationType.ASSIGNMENT_GRADED, UserRole.STUDENT, receiverID, message, createdAt);
    }
}

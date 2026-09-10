package com.example.lms.Notifications.NotificationCreator;

import com.example.lms.common.enums.UserRole;
import com.example.lms.Notifications.NotificationsManager.NotificationData;
import com.example.lms.Notifications.Enums.NotificationType;

import java.util.ArrayList;
import java.util.List;

public class EnrollmentCreator extends NotificationCreator {
    private String enrolledStudentID;
    private String courseInstructorID;

    public EnrollmentCreator(String receiverID, String courseName, String courseInstructorID) {
        super(receiverID, courseName);
        this.enrolledStudentID = enrolledStudentID;
        this.courseInstructorID = courseInstructorID;
    }

    public List<NotificationData> createNewEnrollmentNotification(String loggedInUserID) {
        enrolledStudentID = loggedInUserID;
        String messageToInstructor = "A new student with ID: " + enrolledStudentID + " has enrolled in your course " + courseName + "\n";
        NotificationData notificationData1 = new NotificationData(NotificationType.NEW_ENROLLMENT, UserRole.INSTRUCTOR, courseInstructorID, messageToInstructor, createdAt);

        String messageToStudent = "You have successfully enrolled in " + courseName + " course\n";
        NotificationData notificationData2 = new NotificationData(NotificationType.ENROLLMENT_SUCCESS, UserRole.STUDENT, enrolledStudentID, messageToStudent, createdAt);

        List<NotificationData> notificationDataList = new ArrayList<NotificationData>();
        notificationDataList.add(notificationData1);
        notificationDataList.add(notificationData2);

        return notificationDataList;
    }

    public String getEnrolledStudentID() {
        return enrolledStudentID;
    }
}

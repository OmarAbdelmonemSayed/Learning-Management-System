package com.example.lms.Notifications.NotificationsManager;

import com.example.lms.Notifications.NotificationCreator.AssignmentGradedCreator;
import com.example.lms.Notifications.NotificationCreator.CourseUpdateCreator;
import com.example.lms.Notifications.NotificationCreator.EnrollmentCreator;
import com.example.lms.Notifications.NotificationCreator.QuizGradedCreator;
import com.example.lms.auth.JwtService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class NotificationController {

    private NotificationService notificationService;
    private final JwtService jwtService;

    public NotificationController(NotificationService notificationService, JwtService jwtService) {
        this.notificationService = notificationService;
        this.jwtService = jwtService;
    }

    @PostMapping("{courseID}/success")
    @RolesAllowed({"STUDENT"})
    @ResponseBody
    public List<Notification> PostEnrollmentNotification(@RequestBody EnrollmentCreator enrollmentCreator, HttpServletRequest request, @PathVariable int courseID) throws IOException, InterruptedException {
        List<NotificationData> createdNotificationData;
        createdNotificationData = enrollmentCreator.createNewEnrollmentNotification(extractUserId(request));

        List<Notification> createdNotifications = new ArrayList<>();
        for (NotificationData notificationData : createdNotificationData) {
            createdNotifications.add(notificationService.addNotification(notificationData));
        }

        for (Notification notification : createdNotifications) {
            notificationService.sendNotificationByEmail(notification);
        }

        return createdNotifications;
    }

    @PostMapping("{courseID}/{assignmentID}/mark")
    @RolesAllowed({"INSTRUCTOR"})
    @ResponseBody
    public Notification PostAssignmentGradedNotification(@RequestBody AssignmentGradedCreator assignmentGradedCreator, HttpServletRequest request, @PathVariable int courseID, @PathVariable int assignmentID) throws IOException, InterruptedException {
        Notification generatedNotification = notificationService.addNotification(assignmentGradedCreator.createAssignmentGradedNotification());
        notificationService.sendNotificationByEmail(generatedNotification);
        return generatedNotification;
    }

    @PostMapping("{courseID}/{quizID}/grade")
    @RolesAllowed({"INSTRUCTOR"})
    @ResponseBody
    public Notification PostQuizGradedNotification(@RequestBody QuizGradedCreator quizGradedCreator, HttpServletRequest request, @PathVariable int courseID, @PathVariable int quizID) throws IOException, InterruptedException {
        Notification generatedNotification = notificationService.addNotification(quizGradedCreator.createQuizGradedNotification());
        notificationService.sendNotificationByEmail(generatedNotification);
        return generatedNotification;
    }

    @PostMapping("{courseID}/upload")
    @RolesAllowed({"INSTRUCTOR"})
    @ResponseBody
    public Notification PostCourseUpdateNotification(@RequestBody CourseUpdateCreator courseUpdateCreator, HttpServletRequest request, @PathVariable int courseID) throws IOException, InterruptedException {
        Notification generatedNotification = notificationService.addNotification(courseUpdateCreator.createCourseUpdateNotification());
        notificationService.sendNotificationByEmail(generatedNotification);
        return generatedNotification;
    }




    @GetMapping("notifications")
    @ResponseBody
    public List<Notification> getNotifications(HttpServletRequest request) {
        String userID = extractUserId(request);
        String isUnreadOnly_string = extractHeaderValue(request, "isUnreadOnly");
        boolean isUnreadOnly_boolean = isUnreadOnly_string.equals("true")? true : false;
        return notificationService.getNotifications(userID, isUnreadOnly_boolean);
    }



    @PatchMapping("notifications/{notificationID}")
    @RolesAllowed({"STUDENT", "INSTRUCTOR"})
    public void patchNotification (@PathVariable String notificationID) {
        notificationService.markNotificationAsRead(notificationID);
    }


    private String extractUserId(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing or invalid token");
        }
        String token = authHeader.substring(7);
        String userId = jwtService.extractUsername(token);

        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing or invalid token");
        }

        return userId;
    }

    private String extractHeaderValue(HttpServletRequest request, String headerKey) {
        String headerValue = request.getHeader(headerKey);
        if (headerValue == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing header: " + headerKey);
        }
        return headerValue;
    }

}

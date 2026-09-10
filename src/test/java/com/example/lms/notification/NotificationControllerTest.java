package com.example.lms.notification;

import com.example.lms.Notifications.Enums.NotificationType;
import com.example.lms.auth.JwtService;
import com.example.lms.Notifications.NotificationCreator.EnrollmentCreator;
import com.example.lms.Notifications.NotificationsManager.Notification;
import com.example.lms.Notifications.NotificationsManager.NotificationController;
import com.example.lms.Notifications.NotificationsManager.NotificationData;
import com.example.lms.Notifications.NotificationsManager.NotificationService;
import com.example.lms.common.enums.UserRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class NotificationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(notificationController).build();
        objectMapper = new ObjectMapper(); // Initialize ObjectMapper
    }

//    @Test
//    void testPostEnrollmentNotification() throws Exception {
//        // Register JavaTimeModule to handle LocalDateTime
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.registerModule(new JavaTimeModule());
//
//        // Create EnrollmentCreator object with course name and instructor ID
//        EnrollmentCreator enrollmentCreator = new EnrollmentCreator("3", "Operating Systems", "3");
//
//        // Mock NotificationData creation
//        LocalDateTime now = LocalDateTime.now();
//        String loggedInUserID = "20220016"; // Assuming this is the logged-in student ID
//
//        // Set the logged-in user ID in the EnrollmentCreator
//        enrollmentCreator.createNewEnrollmentNotification(loggedInUserID);
//
//        // Create NotificationData objects with the dynamically set enrolledStudentID
//        NotificationData notificationData1 = new NotificationData(NotificationType.NEW_ENROLLMENT, UserRole.INSTRUCTOR, "3",
//                "A new student with ID: 20220016 has enrolled in your course Operating Systems\n", now);
//        NotificationData notificationData2 = new NotificationData(NotificationType.ENROLLMENT_SUCCESS, UserRole.STUDENT, "20220016",
//                "You have successfully enrolled in Operating Systems course\n", now);
//
//        String createdAtFormatted = "Monday 23/12/2024 7:57 PM";
//        Notification notification1 = new Notification(notificationData1, createdAtFormatted, false);
//        Notification notification2 = new Notification(notificationData2, createdAtFormatted, false);
//
//        // List of notifications
//        List<Notification> notifications = Arrays.asList(notification1, notification2);
//
//        // Mock the behavior of request and jwtService
//        when(request.getHeader("Authorization")).thenReturn("Bearer mockToken"); // Mock Authorization header
//        when(jwtService.extractUsername("mockToken")).thenReturn(loggedInUserID); // Mock JWT token extraction
//        when(notificationService.addNotification(any(NotificationData.class)))
//                .thenReturn(notification1, notification2);
//
//        // Do nothing when sending the notification by email
//        Mockito.doNothing().when(notificationService).sendNotificationByEmail(any(Notification.class));
//
//        // Perform the post request
//        mockMvc.perform(post("/1/success") // Assuming the URL in the controller method is "/{courseID}/success"
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsBytes(enrollmentCreator))) // Serialize to byte array
//                .andExpect(status().isOk()) // Expect 200 status code
//                .andExpect(jsonPath("$[0].notificationID").value("1"))
//                .andExpect(jsonPath("$[1].notificationID").value("2"))
//                .andExpect(jsonPath("$[0].notificationData.receiverID").value("3"))
//                .andExpect(jsonPath("$[1].notificationData.receiverID").value("20220016"));
//
//        // Verify the notificationService methods
//        Mockito.verify(notificationService, times(2)).addNotification(any(NotificationData.class)); // Verify addNotification is called twice
//        Mockito.verify(notificationService, times(2)).sendNotificationByEmail(any(Notification.class)); // Verify sendNotificationByEmail is called twice
//    }





    @Test
    void testGetNotifications() {
        // Arrange
        String mockUserId = "12345";
        String isUnreadOnly = "true";
        List<Notification> mockNotifications = Collections.emptyList();

        when(request.getHeader("Authorization")).thenReturn("Bearer mockToken");
        when(request.getHeader("isUnreadOnly")).thenReturn(isUnreadOnly);
        when(jwtService.extractUsername("mockToken")).thenReturn(mockUserId);
        when(notificationService.getNotifications(mockUserId, true)).thenReturn(mockNotifications);

        // Act
        List<Notification> result = notificationController.getNotifications(request);

        // Assert
        assertNotNull(result);
        assertEquals(mockNotifications, result);
        verify(notificationService).getNotifications(mockUserId, true);
    }

    @Test
    void testPatchNotification() throws Exception {
        Mockito.doNothing().when(notificationService).markNotificationAsRead("1");

        mockMvc.perform(patch("/notifications/1"))
                .andExpect(status().isOk());
    }

}
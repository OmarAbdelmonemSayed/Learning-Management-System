package com.example.lms.PerformanceTracking;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.lms.PerformanceTracking.dto.StudentPerformanceDTO;
import com.example.lms.auth.JwtService;
import com.example.lms.common.enums.UserRole;
import com.example.lms.course.Course;
import com.example.lms.course.CourseService;
import com.example.lms.quiz.QuizAttemptService;
import com.example.lms.submission.SubmissionService;
import com.example.lms.user.User;
import com.example.lms.user.UserRepository;

class PerformanceTrackingServiceTest {
    @Spy
    @InjectMocks
    private PerformanceTrackingService performanceTrackingService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PerformanceTrackingRepository performanceTrackingRepository;

    @Mock
    private SubmissionService submissionService;

    @Mock
    private CourseService courseService;

    @Mock
    private QuizAttemptService quizAttemptService;

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetStudentPerformanceWithValidInstructor() {
        String studentId = "student1";
        String instructorId = "instructor1";
        User instructor = new User();
        instructor.setId(instructorId);
        instructor.setRole(UserRole.INSTRUCTOR);

        User student = new User();
        student.setId(studentId);
        student.setRole(UserRole.STUDENT);

        List<Course> courses = new ArrayList<>();

        when(request.getHeader("Authorization")).thenReturn("Bearer mockToken");
        when(jwtService.extractUsername("mockToken")).thenReturn(instructorId);
        when(userRepository.findById(instructorId)).thenReturn(Optional.of(instructor));
        when(userRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(performanceTrackingRepository.findCoursesByInstructorAndStudent(instructorId, studentId)).thenReturn(courses);

        ResponseEntity<?> response = performanceTrackingService.getStudentPerformance(studentId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof List<?>);
    }

    @Test
    void testGetStudentPerformanceWithInvalidInstructor() {
        String instructorId = "instructor1";

        when(request.getHeader("Authorization")).thenReturn("Bearer mockToken");
        when(jwtService.extractUsername("mockToken")).thenReturn(instructorId);
        when(userRepository.findById(instructorId)).thenReturn(Optional.empty());

        ResponseEntity<?> response = performanceTrackingService.getStudentPerformance("student1", request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid Instructor ID", response.getBody());
    }

    @Test
    void testGetStudentReportWithInvalidUserRole() throws Exception {
        String studentId = "student1";
        String adminId = "admin1";
        User admin = new User();
        admin.setId(adminId);
        admin.setRole(UserRole.STUDENT);

        when(request.getHeader("Authorization")).thenReturn("Bearer mockToken");
        when(jwtService.extractUsername("mockToken")).thenReturn(adminId);
        when(userRepository.findById(adminId)).thenReturn(Optional.of(admin));

        ResponseEntity<?> response = performanceTrackingService.getStudentReport(studentId, request, this.response);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("User is a Student", response.getBody());
    }
}

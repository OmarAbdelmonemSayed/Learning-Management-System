package com.example.lms.submission;

import com.example.lms.auth.JwtService;
import com.example.lms.common.enums.UserRole;
import com.example.lms.user.User;
import com.example.lms.user.UserRepository;
import com.example.lms.course.Course;
import com.example.lms.course.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SubmissionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SubmissionService submissionService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private CourseService courseService;

    @InjectMocks
    private SubmissionController submissionController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(submissionController).build();
    }

    @Test
    void testSubmitAssignmentWithFile() throws Exception {
        // إعداد البيانات
        String courseId = "course1";
        String assignmentId = "assignment1";
        String token = "validToken";
        String userId = "user1";

        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "assignment1.pdf", MediaType.APPLICATION_PDF_VALUE, "Test file content".getBytes()
        );

        User user = new User();
        user.setId(userId);
        user.setRole(UserRole.STUDENT);

        Course course = new Course();
        course.setStudents(Collections.singletonList(user));

        Submission submission = new Submission();
        submission.setId(1L);
        submission.setFilePath("uploads/assignment1.pdf");
        submission.setAssignmentId(assignmentId);
        submission.setStudentId(userId);
        submission.setSubmittedDate(LocalDateTime.now());

        // Mocking
        when(jwtService.extractUsername(token)).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(courseService.getCourseById(courseId)).thenReturn(course);
        when(submissionService.submitAssignmentWithFile(Mockito.any(Submission.class), Mockito.any())).thenReturn(submission);

        // تنفيذ الطلب واختباره
        mockMvc.perform(multipart("/courses/{courseId}/assignments/{assignmentId}/submissions", courseId, assignmentId)
                        .file(mockFile)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated());
    }


    @Test
    void testAddFeedback() throws Exception {
        Long submissionId = 1L;
        String feedback = "Good job!";
        String userId = "instructor1";

        // Mock JWT and UserRepository behavior
        when(jwtService.extractUsername(anyString())).thenReturn(userId);
        User instructor = new User();
        instructor.setRole(UserRole.INSTRUCTOR);
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(instructor));

        // Mock submissionService behavior
        Submission submission = new Submission();
        submission.setId(submissionId);
        submission.setFeedback(feedback);
        when(submissionService.addFeedback(submissionId, feedback)).thenReturn(submission);

        // Perform the POST request to add feedback
        mockMvc.perform(post("/courses/{courseId}/assignments/{assignmentId}/submissions/{submissionId}/feedback", "course1", "assignment1", submissionId)
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"feedback\": \"" + feedback + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.feedback").value(feedback));
    }

    @Test
    void testGradeAssignment() throws Exception {
        Long submissionId = 1L;
        Long grade = 85L;
        String userId = "instructor1";

        // Mock JWT and UserRepository behavior
        when(jwtService.extractUsername(anyString())).thenReturn(userId);
        User instructor = new User();
        instructor.setRole(UserRole.INSTRUCTOR);
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(instructor));

        // Mock submissionService behavior
        Submission submission = new Submission();
        submission.setId(submissionId);
        submission.setGrade(grade);
        when(submissionService.gradeAssignment(submissionId, grade)).thenReturn(submission);

        // Perform the POST request to grade assignment
        mockMvc.perform(post("/courses/{courseId}/assignments/{assignmentId}/submissions/{submissionId}/grade", "course1", "assignment1", submissionId)
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"grade\": \"" + grade + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.grade").value(grade));
    }

}

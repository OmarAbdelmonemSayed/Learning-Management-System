package com.example.lms.PerformanceTracking;

import com.example.lms.PerformanceTracking.dto.StudentDto;
import com.example.lms.PerformanceTracking.PerformanceTrackingService;
import com.example.lms.auth.JwtService;
import com.example.lms.user.User;
import com.example.lms.common.enums.UserRole;
import com.example.lms.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PerformanceTrackingControllerTest {

    @Mock
    private PerformanceTrackingService performanceTrackingService;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PerformanceTrackingController performanceTrackingController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(performanceTrackingController).build();
    }

    @Test
    void getStudentPerformance_ShouldReturn200() throws Exception {
        // Arrange
        String instructorId = "instructor123";
        String studentId = "student123";
        StudentDto studentDto = new StudentDto(studentId);

        // Mock behavior for JWT extraction and user lookup
        when(jwtService.extractUsername(anyString())).thenReturn(instructorId);

        // Mock the user repository to return a user with INSTRUCTOR role
        User mockUser = new User();
        mockUser.setId(instructorId);
        mockUser.setRole(UserRole.INSTRUCTOR);
        when(userRepository.findById(instructorId)).thenReturn(java.util.Optional.of(mockUser));

        // Simulate performance tracking logic
        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            String studentIdArg = (String) args[0];
            // Return a mock response based on the studentId
            return ResponseEntity.ok("Performance Data for " + studentIdArg);
        }).when(performanceTrackingService).getStudentPerformance(eq(studentId), any());

        // Prepare the request body as JSON
        String jsonRequest = "{\"studentId\":\"" + studentId + "\"}";

        // Act & Assert
        mockMvc.perform(get("/students/performance")
                .contentType("application/json")
                .header("Authorization", "Bearer validToken")
                .content(jsonRequest))
                .andExpect(status().isOk())  // Expect 200 OK
                .andExpect(jsonPath("$").value("Performance Data for " + studentId));  // Expected response

        // Verify the service was called with the correct parameters
        verify(performanceTrackingService, times(1)).getStudentPerformance(eq(studentId), any());
    }

    @Test
    void getStudentReport_ShouldReturn200() throws Exception {
        // Arrange
        String instructorId = "instructor123";
        String studentId = "student123";

        // Mock behavior for JWT extraction and user lookup
        when(jwtService.extractUsername(anyString())).thenReturn(instructorId);

        // Mock the user repository to return a user with INSTRUCTOR role
        User mockUser = new User();
        mockUser.setId(instructorId);
        mockUser.setRole(UserRole.INSTRUCTOR);
        when(userRepository.findById(instructorId)).thenReturn(java.util.Optional.of(mockUser));

        // Simulate the report generation logic
        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            String studentIdArg = (String) args[0];
            return ResponseEntity.ok("Report for " + studentIdArg);
        }).when(performanceTrackingService).getStudentReport(eq(studentId), any(), any());

        // Act & Assert
        mockMvc.perform(get("/students/{studentId}/reports", studentId)  // Updated URL to use PathVariable
                .contentType("application/json")
                .header("Authorization", "Bearer validToken"))
                .andExpect(status().isOk())  // Expect 200 OK
                .andExpect(header().string("Content-Disposition", "attachment; filename=student_report.xlsx"))  // Verify correct header
                .andExpect(content().contentType("application/vnd.ms-excel"))  // Verify content type for Excel
                .andExpect(content().string("Report for " + studentId));  // Expected response content

        // Verify the service was called with the correct parameters
        verify(performanceTrackingService, times(1)).getStudentReport(eq(studentId), any(), any());
    }
}

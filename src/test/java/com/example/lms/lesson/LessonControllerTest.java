package com.example.lms.lesson;

import com.example.lms.auth.JwtService;
import com.example.lms.user.User;
import com.example.lms.common.enums.UserRole;
import com.example.lms.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class LessonControllerTest {

    @Mock
    private LessonService lessonService;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LessonController lessonController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(lessonController).build();
    }

    @Test
    void testCreateLesson() throws Exception {
        String instructorId = "instructor123";
        LessonDto lessonDto = new LessonDto();
        lessonDto.setName("Lesson Title");
        lessonDto.setCourseId("course123");

        Lesson lesson = new Lesson();
        lesson.setId("lesson123");
        lesson.setName("Lesson Title");

        when(jwtService.extractUsername(anyString())).thenReturn(instructorId);

        User mockUser = new User();
        mockUser.setId(instructorId);
        mockUser.setRole(UserRole.INSTRUCTOR);
        when(userRepository.findById(instructorId)).thenReturn(java.util.Optional.of(mockUser));

        when(lessonService.createLesson(any(LessonDto.class), eq(instructorId))).thenReturn(lesson);

        String jsonRequest = "{\"name\":\"Lesson Title\", \"courseId\":\"course123\"}";

        mockMvc.perform(post("/lessons/create")
                .contentType("application/json")
                .header("Authorization", "Bearer validToken")
                .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("lesson123"))
                .andExpect(jsonPath("$.name").value("Lesson Title"));

        verify(lessonService, times(1)).createLesson(any(LessonDto.class), eq(instructorId));
    }


    @Test
    void testGenerateOtp() throws Exception {
        String instructorId = "instructor123";
        String lessonId = "lesson123";

        when(jwtService.extractUsername(anyString())).thenReturn(instructorId);

        User mockUser = new User();
        mockUser.setId(instructorId);
        mockUser.setRole(UserRole.INSTRUCTOR);
        when(userRepository.findById(instructorId)).thenReturn(java.util.Optional.of(mockUser));

        Lesson lesson = new Lesson();
        lesson.setId(lessonId);
        lesson.setOtp("otp123");
        
        doReturn(lesson).when(lessonService).generateOtp(eq(lessonId), eq(instructorId));

        // Act & Assert
        mockMvc.perform(post("/lessons/{lessonId}/generate-otp", lessonId)
                .header("Authorization", "Bearer validToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.otp").value("otp123"));

        verify(lessonService, times(1)).generateOtp(eq(lessonId), eq(instructorId));
    }

    @Test
    void testAttendLesson() throws Exception {
        String studentId = "student123";
        String lessonId = "lesson123";

        when(jwtService.extractUsername(anyString())).thenReturn(studentId);

        User mockUser = new User();
        mockUser.setId(studentId);
        mockUser.setRole(UserRole.STUDENT);
        when(userRepository.findById(studentId)).thenReturn(java.util.Optional.of(mockUser));

        doNothing().when(lessonService).attendLesson(eq(lessonId), eq("otp123"), eq(studentId));

        String jsonRequest = "{\"otp\":\"otp123\"}";

        mockMvc.perform(post("/lessons/{lessonId}/attend", lessonId)
                .contentType("application/json")
                .header("Authorization", "Bearer validToken")
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(content().string("Attendance marked successfully"));

        verify(lessonService, times(1)).attendLesson(eq(lessonId), eq("otp123"), eq(studentId));
    }
}

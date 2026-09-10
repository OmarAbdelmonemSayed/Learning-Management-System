package com.example.lms.course;

import com.example.lms.auth.JwtService;
import com.example.lms.common.enums.UserRole;
import com.example.lms.lesson.Lesson;
import com.example.lms.lesson.LessonDto;
import com.example.lms.user.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CourseControllerTest {

    @Mock
    private CourseService courseService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private CourseController courseController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(courseController).build();
    }

    @Test
    void testGetAvailableCourses() throws Exception {
        String userId = "user123";
        when(jwtService.extractUsername(anyString())).thenReturn(userId);
        when(courseService.getAvailableCourses(userId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/courses/available")
                .header("Authorization", "Bearer validToken"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        
        verify(courseService, times(1)).getAvailableCourses(userId);
    }

    @Test
    void testGetRelatedCourses() throws Exception {
        String userId = "user123";
        when(jwtService.extractUsername(anyString())).thenReturn(userId);
        when(courseService.getRelatedCourses(userId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/courses/related")
                .header("Authorization", "Bearer validToken"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        
        verify(courseService, times(1)).getRelatedCourses(userId);
    }

    @Test
    void testEnrollInCourse() throws Exception {
        String userId = "user123";
        String courseId = "course123";
        doNothing().when(courseService).enrollInCourse(courseId, userId);
        when(jwtService.extractUsername(anyString())).thenReturn(userId);

        mockMvc.perform(post("/courses/{courseId}/enroll", courseId)
                .header("Authorization", "Bearer validToken"))
                .andExpect(status().isOk())
                .andExpect(content().string("Enrolled successfully"));
        
        verify(courseService, times(1)).enrollInCourse(courseId, userId);
    }

    @Test
    void testCreateCourse() throws Exception {
        String userId = "user123";

        CourseDto courseDto = new CourseDto();
        courseDto.setTitle("Course Title");
        courseDto.setDescription("Course Description");
        courseDto.setDuration(30);

        Course course = new Course();
        course.setId("course123");
        course.setTitle("Course Title");
        course.setDescription("Course Description");
        course.setDuration(30);
        course.setInstructor(null);
        course.setStudents(Collections.emptyList());
        course.setLessons(Collections.emptyList());

        when(courseService.createCourse(any(CourseDto.class), eq(userId))).thenReturn(course);

        when(jwtService.extractUsername(anyString())).thenReturn(userId);

        String jsonRequest = "{\"title\":\"Course Title\",\"description\":\"Course Description\",\"duration\":30}";

        mockMvc.perform(post("/courses/create")
                .contentType("application/json")
                .header("Authorization", "Bearer validToken")
                .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("course123"))
                .andExpect(jsonPath("$.title").value("Course Title"))
                .andExpect(jsonPath("$.description").value("Course Description"));

        verify(courseService, times(1)).createCourse(any(CourseDto.class), eq(userId));
    }

    @Test
    void testGetEnrolledStudents() throws Exception {
        String userId = "user123";
        String courseId = "course123";
        when(jwtService.extractUsername(anyString())).thenReturn(userId);
        when(courseService.getEnrolledStudents(courseId, userId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/courses/{courseId}/students", courseId)
                .header("Authorization", "Bearer validToken"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        
        verify(courseService, times(1)).getEnrolledStudents(courseId, userId);
    }

    @Test
    void testAddLesson() throws Exception {
        String userId = "user123";
        String courseId = "course123";

        LessonDto lessonDto = new LessonDto();
        lessonDto.setName("Lesson Title");
        lessonDto.setCourseId(courseId);
        
        doNothing().when(courseService).addLesson(eq(courseId), any(Lesson.class), eq(userId));

        when(jwtService.extractUsername(anyString())).thenReturn(userId);

        String jsonRequest = "{\"name\":\"Lesson Title\", \"courseId\":\"" + courseId + "\"}";

        mockMvc.perform(post("/courses/{courseId}/add-lesson", courseId)
                .contentType("application/json")
                .header("Authorization", "Bearer validToken")
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(content().string("Lesson added successfully"));

        verify(courseService, times(1)).addLesson(eq(courseId), any(Lesson.class), eq(userId));
    }

    @Test
    void testAddLesson_InvalidCourseId() throws Exception {
        String userId = "user123";
        String invalidCourseId = "invalidCourseId";

        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"))
            .when(courseService).addLesson(eq(invalidCourseId), any(Lesson.class), eq(userId));

        when(jwtService.extractUsername(anyString())).thenReturn(userId);

        String jsonRequest = "{\"name\":\"Lesson Title\", \"courseId\":\"" + invalidCourseId + "\"}";

        mockMvc.perform(post("/courses/{courseId}/add-lesson", invalidCourseId)
                .contentType("application/json")
                .header("Authorization", "Bearer validToken")
                .content(jsonRequest))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Course not found"));

        verify(courseService, times(1)).addLesson(eq(invalidCourseId), any(Lesson.class), eq(userId));
    }
    
    @Test
    void testGetCourseById() throws Exception {
        // Arrange
        String userId = "user123";
        String courseId = "course123";

        User instructor = new User();
        instructor.setRole(UserRole.INSTRUCTOR);
        instructor.setId("instructor123");
        instructor.setName("Instructor Name");
        instructor.setEmail("instructor@example.com");
        instructor.setPassword("password");

        Course course = new Course();
        course.setId(courseId);
        course.setTitle("Course Title");
        course.setDescription("Course Description");
        course.setDuration(30);
        course.setInstructor(instructor);
        course.setStudents(null);

        when(jwtService.extractUsername(anyString())).thenReturn(userId);
        when(courseService.getCourseById(courseId, userId)).thenReturn(course);

        mockMvc.perform(get("/courses/{courseId}", courseId)
                .header("Authorization", "Bearer validToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(courseId))
                .andExpect(jsonPath("$.title").value("Course Title"))
                .andExpect(jsonPath("$.instructor.id").value("instructor123"))
                .andExpect(jsonPath("$.students").doesNotExist())
                .andExpect(jsonPath("$.lessons").exists());

        verify(courseService, times(1)).getCourseById(courseId, userId);
    }

    @Test
    void testGetCourseById_NotFound() throws Exception {
        String userId = "user123";
        String courseId = "nonexistentCourseId";

        when(jwtService.extractUsername(anyString())).thenReturn(userId);
        when(courseService.getCourseById(courseId, userId)).thenThrow(new RuntimeException("Course not found"));

        mockMvc.perform(get("/courses/{courseId}", courseId)
                .header("Authorization", "Bearer validToken"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Course not found"));

        verify(courseService, times(1)).getCourseById(courseId, userId);
    }
    
    @Test
    void testDeleteCourse() throws Exception {
        String courseId = "course123";
        String userId = "user123";

        when(jwtService.extractUsername(anyString())).thenReturn(userId);
        doNothing().when(courseService).deleteCourse(courseId, userId);

        mockMvc.perform(delete("/courses/{courseId}", courseId)
                .header("Authorization", "Bearer validToken"))
                .andExpect(status().isOk())
                .andExpect(content().string("Course deleted successfully"));

        verify(courseService, times(1)).deleteCourse(courseId, userId);
    }
}

package com.example.lms.question;

import com.example.lms.auth.JwtService;
import com.example.lms.course.CourseService;
import com.example.lms.user.User;
import com.example.lms.common.enums.UserRole;
import com.example.lms.user.UserRepository;
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

class QuestionControllerTest {

    @Mock
    private QuestionService questionService;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private QuestionController questionController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(questionController).build();
    }

    @Test
    void testGetAllQuestionsInQuestionBank() throws Exception {
        String courseId = "course123";
        when(questionService.getAllQuestionsByCourse(courseId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/questions/{courseId}/question-bank", courseId))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(questionService, times(1)).getAllQuestionsByCourse(courseId);
    }

    @Test
    void testAddQuestionToQuestionBank() throws Exception {
        String courseId = "course123";
        String userId = "user123";

        QuestionDTO questionDTO = new QuestionDTO();
        questionDTO.setQuestionType("MCQ");
        questionDTO.setQuestionText("Sample Question");
        questionDTO.setAnswer("Answer");

        doNothing().when(questionService).createQuestion(eq(courseId), eq(questionDTO), eq(userId));
        when(jwtService.extractUsername(anyString())).thenReturn(userId);

        String jsonRequest = "{\"questionType\":\"MCQ\",\"questionText\":\"Sample Question\",\"answer\":\"Answer\"}";

        mockMvc.perform(post("/questions/{courseId}/question-bank", courseId)
                .contentType("application/json")
                .header("Authorization", "Bearer validToken")
                .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(content().string("Question added to the question bank successfully!"));

        verify(questionService, times(1)).createQuestion(eq(courseId), eq(questionDTO), eq(userId));
    }

    @Test
    void testUpdateQuestionInQuestionBank() throws Exception {
        String courseId = "course123";
        String questionId = "question123";
        String userId = "user123";

        QuestionDTO questionDTO = new QuestionDTO();
        questionDTO.setQuestionType("MCQ");
        questionDTO.setQuestionText("Updated Question");
        questionDTO.setAnswer("Updated Answer");

        doNothing().when(questionService).updateQuestion(eq(courseId), eq(questionId), eq(questionDTO), eq(userId));
        when(jwtService.extractUsername(anyString())).thenReturn(userId);

        String jsonRequest = "{\"questionType\":\"MCQ\",\"questionText\":\"Updated Question\",\"answer\":\"Updated Answer\"}";

        mockMvc.perform(put("/questions/{courseId}/question-bank/{questionId}", courseId, questionId)
                .contentType("application/json")
                .header("Authorization", "Bearer validToken")
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(content().string("Question updated successfully in the question bank!"));

        verify(questionService, times(1)).updateQuestion(eq(courseId), eq(questionId), eq(questionDTO), eq(userId));
    }

    @Test
    void testDeleteQuestionFromQuestionBank() throws Exception {
        String courseId = "course123";
        String questionId = "question123";
        String userId = "user123";

        doNothing().when(questionService).deleteQuestion(eq(courseId), eq(questionId), eq(userId));
        when(jwtService.extractUsername(anyString())).thenReturn(userId);

        mockMvc.perform(delete("/questions/{courseId}/question-bank/{questionId}", courseId, questionId)
                .header("Authorization", "Bearer validToken"))
                .andExpect(status().isOk())
                .andExpect(content().string("Question deleted successfully from the question bank!"));

        verify(questionService, times(1)).deleteQuestion(eq(courseId), eq(questionId), eq(userId));
    }
}

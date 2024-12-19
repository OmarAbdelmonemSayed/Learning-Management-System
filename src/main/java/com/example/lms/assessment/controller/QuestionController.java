package com.example.lms.assessment.controller;

import com.example.lms.assessment.dto.QuestionDTO;
import com.example.lms.assessment.service.QuestionService;
import com.example.lms.assessment.service.QuizService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/courses/{courseId}/quizzes/{quizId}/questions")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuizService quizService;

    @PostMapping
    public ResponseEntity<String> createQuestion(@PathVariable String courseId,
                                                 @PathVariable String quizId,
                                                 @RequestBody @Valid QuestionDTO questionDTO) {

        if (!quizService.existsById(courseId, quizId)) {
            return ResponseEntity.status(404).body("Quiz not found with the given quizId.");
        }


        questionDTO.setCourseId(courseId);
        questionDTO.setQuizId(quizId);

        questionService.createQuestion(questionDTO);
        return ResponseEntity.status(201).body("Question created successfully!");
    }

    @GetMapping("/{questionId}")
    public ResponseEntity<QuestionDTO> getQuestion(@PathVariable String courseId,
                                              @PathVariable String quizId,
                                              @PathVariable String questionId) {

        if (!quizService.existsById(courseId, quizId)) {
            return ResponseEntity.status(404).body(null);
        }

        if (!questionService.existsById(courseId, quizId, questionId)) {
            return ResponseEntity.status(404).body(null);
        }

        QuestionDTO question = questionService.getQuestionByQuizAndId(courseId, quizId, questionId);

        return ResponseEntity.status(200).body(question);
    }

    @GetMapping
    public ResponseEntity<List<QuestionDTO>> getAllQuestions(@PathVariable String courseId,
                                                             @PathVariable String quizId) {

        if (!quizService.existsById(courseId, quizId)) {
            return ResponseEntity.status(404).body(null);
        }


        List<QuestionDTO> questions = questionService.getAllQuestionsByQuiz(courseId, quizId);
        return ResponseEntity.ok(questions);
    }

    @PutMapping("/{questionId}")
    public ResponseEntity<String> updateQuestion(@PathVariable String courseId,
                                                 @PathVariable String quizId,
                                                 @PathVariable String questionId,
                                                 @RequestBody @Valid QuestionDTO questionDTO) {

        if (!quizService.existsById(courseId, quizId)) {
            return ResponseEntity.status(404).body("Quiz not found with the given quizId.");
        }

        if (!questionService.existsById(courseId, quizId, questionId)) {
            return ResponseEntity.status(404).body("Question not found with the given questionId.");
        }

        questionDTO.setCourseId(courseId);
        questionDTO.setQuizId(quizId);
        questionService.updateQuestion(questionId, questionDTO);
        return ResponseEntity.ok("Question updated successfully!");
    }

    @DeleteMapping("/{questionId}")
    public ResponseEntity<String> deleteQuestion(@PathVariable String courseId,
                                                 @PathVariable String quizId,
                                                 @PathVariable String questionId) {

        if (!quizService.existsById(courseId, quizId)) {
            return ResponseEntity.status(404).body("Quiz not found with the given quizId.");
        }

        if (!questionService.existsById(courseId, quizId, questionId)) {
            return ResponseEntity.status(404).body("Question not found with the given questionId.");
        }

        questionService.deleteQuestion(questionId);
        return ResponseEntity.ok("Question deleted successfully!");
    }
}

package com.example.lms.assessment.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import com.example.lms.assessment.service.QuizService;
import com.example.lms.assessment.dto.QuizDTO;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/courses/{courseId}/quizzes")
@Validated
public class QuizController {

    @Autowired
    private QuizService quizService;

    @PostMapping
    public ResponseEntity<String> createQuiz(@PathVariable String courseId, @Valid @RequestBody QuizDTO quizDTO) {
        quizDTO.setCourseId(courseId);
        quizService.createQuiz(quizDTO);
        return ResponseEntity.status(201).body("Quiz created successfully!");
    }

    @GetMapping("/{quizId}")
    public ResponseEntity<QuizDTO> getQuiz(@PathVariable String courseId, @PathVariable String quizId) {
        Optional<QuizDTO> quiz = quizService.getQuizByCourseAndId(courseId, quizId);

        if (quiz.isPresent()) {
            return ResponseEntity.status(200).body(quiz.get());
        }
        else {
            return ResponseEntity.status(404).body(null);
        }
    }

    @GetMapping
    public ResponseEntity<List<QuizDTO>> getAllQuizzes(@PathVariable String courseId) {
        List<QuizDTO> quizzes = quizService.getAllQuizzesByCourse(courseId);
        return ResponseEntity.ok(quizzes);
    }

    @PutMapping("/{quizId}")
    public ResponseEntity<String> updateQuiz(@PathVariable String courseId, @PathVariable String quizId, @Valid @RequestBody QuizDTO quizDTO) {

        if (!quizService.existsById(courseId, quizId)) {
            return ResponseEntity.status(404).body("Quiz not found");
        }

        quizDTO.setCourseId(courseId);

        quizService.updateQuiz(courseId, quizId, quizDTO);

        return ResponseEntity.status(200).body("Quiz updated successfully");
    }


    @DeleteMapping("/{quizId}")
    public ResponseEntity<String> deleteQuiz(@PathVariable String courseId, @PathVariable String quizId) {
        quizService.deleteQuiz(courseId, quizId);
        return ResponseEntity.status(204).body("Quiz deleted successfully!");
    }
}

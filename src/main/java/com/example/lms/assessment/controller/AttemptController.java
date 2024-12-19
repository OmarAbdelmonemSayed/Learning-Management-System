package com.example.lms.assessment.controller;

import com.example.lms.assessment.dto.AttemptDTO;
import com.example.lms.assessment.service.AttemptService;
import com.example.lms.assessment.service.QuizService;
import com.example.lms.assessment.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@Validated
@RestController
@RequestMapping("/{studentId}/courses/{courseId}/quizzes/{quizId}/attempts")
public class AttemptController {

    @Autowired
    private AttemptService attemptService;

    @Autowired
    private QuizService quizService;

    @Autowired
    private QuestionService questionService;

    @PostMapping
    public ResponseEntity<String> submitAttempt(@PathVariable String studentId,
                                                @PathVariable String courseId,
                                                @PathVariable String quizId,
                                                @RequestBody @Valid AttemptDTO attemptDTO) {

        if (!quizService.existsById(courseId, quizId)) {
            return ResponseEntity.status(404).body("Quiz not found with the given quizId.");
        }

        if (attemptDTO.getAnswers().size() != questionService.getAllQuestionsByQuiz(courseId, quizId).size()) {
            return ResponseEntity.status(400).body("The number of answers does not match the number of questions.");
        }

        attemptService.submitAttempt(studentId, quizId, attemptDTO);
        return ResponseEntity.status(201).body("Quiz attempt submitted successfully!");
    }


    @GetMapping("/{attemptId}/results")
    public ResponseEntity<AttemptDTO> getAttemptResult(@PathVariable String studentId,
                                                       @PathVariable String courseId,
                                                       @PathVariable String quizId,
                                                       @PathVariable String attemptId) {

        if (!quizService.existsById(courseId, quizId)) {
            return ResponseEntity.status(404).body(null);
        }

        AttemptDTO result = attemptService.getAttemptResult(studentId, quizId, attemptId);

        if (result == null) {
            return ResponseEntity.status(404).body(null);
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/results")
    public ResponseEntity<List<AttemptDTO>> getAllAttemptResults(@PathVariable String studentId,
                                                                 @PathVariable String courseId,
                                                                 @PathVariable String quizId) {

        if (!quizService.existsById(courseId, quizId)) {
            return ResponseEntity.status(404).body(null);
        }

        List<AttemptDTO> allResults = attemptService.getAllAttemptResults(studentId, quizId);

        if (allResults.isEmpty()) {
            return ResponseEntity.status(404).body(null);
        }

        return ResponseEntity.ok(allResults);
    }

    @DeleteMapping("/{attemptId}")
    public ResponseEntity<String> deleteAttempt(@PathVariable String studentId,
                                                @PathVariable String courseId,
                                                @PathVariable String quizId,
                                                @PathVariable String attemptId) {

        if (!quizService.existsById(courseId, quizId)) {
            return ResponseEntity.status(404).body("Quiz not found with the given quizId.");
        }

        if (!attemptService.existsById(studentId, quizId, attemptId)) {
            return ResponseEntity.status(404).body("Attempt not found with the given attemptId.");
        }

        attemptService.deleteAttempt(studentId, quizId, attemptId);

        return ResponseEntity.ok("Attempt deleted successfully!");
    }

}

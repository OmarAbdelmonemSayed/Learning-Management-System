package com.example.lms.quiz;

import com.example.lms.auth.JwtService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.catalina.filters.ExpiresFilter;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/quizzes/{courseId}")
public class QuizController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private QuizService quizService;

    @Autowired
    private QuizAttemptService quizAttemptService;

    @RolesAllowed({"INSTRUCTOR"})
    @PostMapping
    public ResponseEntity<?> createQuiz(@PathVariable String courseId, @RequestParam String title, @RequestParam int questionCount, HttpServletRequest request) {

        String userId = extractUserId(request);

        Quiz quiz = quizService.createQuiz(courseId, title, questionCount, userId);

        return ResponseEntity.ok("Quiz created successfully with ID: " + quiz.getId());
    }

    @RolesAllowed({"INSTRUCTOR"})
    @DeleteMapping("/{quizId}")
    public ResponseEntity<?> deleteQuiz(@PathVariable String courseId, @PathVariable String quizId, HttpServletRequest request) {

        String userId = extractUserId(request);

        quizService.deleteQuiz(courseId, quizId, userId);
        return ResponseEntity.ok("Quiz deleted successfully");
    }

    @RolesAllowed({"INSTRUCTOR"})
    @GetMapping("/{quizId}")
    public ResponseEntity<?> getQuizForInstructor(@PathVariable String courseId, @PathVariable String quizId, HttpServletRequest request) {

        String userId = extractUserId(request);

        return ResponseEntity.ok(quizService.getQuizForInstructor(courseId, quizId, userId));
    }

    @RolesAllowed({"STUDENT"})
    @GetMapping("/{quizId}/students")
    public ResponseEntity<?> getQuizForStudent(@PathVariable String courseId, @PathVariable String quizId, HttpServletRequest request) {

        String userId = extractUserId(request);

        return ResponseEntity.ok(quizService.getQuizForStudent(courseId, quizId, userId));
    }


    @RolesAllowed({"STUDENT"})
    @PostMapping("/{quizId}/students/{studentId}")
    public ResponseEntity<?> startQuizForStudent(@PathVariable String courseId, @PathVariable String quizId, @PathVariable String studentId,
                                                 @RequestBody QuizAttemptDTO attemptDTO,
                                                 HttpServletRequest request) {

        String userId = extractUserId(request);

        quizAttemptService.submit(courseId, studentId, quizId, attemptDTO, userId);
        return ResponseEntity.ok(quizAttemptService.getResults(courseId, studentId, quizId));
    }


    @GetMapping("/{quizId}/students/{studentId}/result")
    public ResponseEntity<?> showQuizResultForStudent(@PathVariable String courseId, @PathVariable String quizId, @PathVariable String studentId) {


        return ResponseEntity.ok(quizAttemptService.getResults(courseId, studentId, quizId));
    }

    private String extractUserId(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing or invalid token");
        }
        String token = authHeader.substring(7);
        String userId = jwtService.extractUsername(token);

        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing or invalid token");
        }

        return userId;
    }
}

package com.example.lms.quiz;

import com.example.lms.course.CourseRepository;
import com.example.lms.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class QuizAttemptService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizAttemptRepository quizAttemptRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    public boolean quizAttempt(String courseId, String studentId, String quizId) {
        Optional<QuizAttempt> attempt = quizAttemptRepository.findQuizAttemptByCourseIdAndStudentIdAndQuizId(courseId, studentId, quizId);
        return attempt.isPresent();
    }


    public QuizAttempt submit(String courseId, String studentId, String quizId, QuizAttemptDTO attemptDTO, String userId) {

        if (courseRepository.findById(courseId).isEmpty()) {
            throw new RuntimeException("Course not found with the given courseId.");
        }

        if (userRepository.findById(studentId).isEmpty()) {
            throw new RuntimeException("Student not found with the given studentId.");
        }


        if (quizRepository.findByCourseIdAndId(courseId, quizId).isEmpty()) {
            throw new RuntimeException("Quiz with ID: " + quizId + " does not exist");
        }

        if (quizAttempt(courseId, studentId, quizId)) {
            throw new RuntimeException("You have already attempted this quiz.");
        }

        QuizAttempt quizAttempt = new QuizAttempt();
        quizAttempt.setCourseId(courseId);
        quizAttempt.setQuiz(quizRepository.findByCourseIdAndId(courseId, quizId).get());
        quizAttempt.setStudentId(studentId);
        quizAttempt.setAnswers(attemptDTO.getAnswers());
        quizAttempt.setScore(calculateScore(courseId, studentId, quizId, attemptDTO));
        quizAttempt.setAttemptTime(LocalDateTime.now());
        return quizAttemptRepository.save(quizAttempt);
    }


    public int calculateScore(String courseId, String studentId, String quizId, QuizAttemptDTO attemptDTO) {
        int score = 0;
        Optional<Quiz> quiz = quizRepository.findByCourseIdAndId(courseId, quizId);
        if (quiz.isPresent()) {
            for (int i = 0; i < attemptDTO.getAnswers().size(); i++) {
                if (attemptDTO.getAnswers().get(i).toUpperCase().equals(quiz.get().getQuestions().get(i).getAnswer().toUpperCase())) {
                    score++;
                }
            }
        }
        return score;
    }

    public QuizAttemptResultsDTO getResults(String courseId, String studentId, String quizId) {

        if (!quizAttempt(courseId, studentId, quizId)) {
            throw new RuntimeException("This student haven't already attempted this quiz.");
        }

        if (courseRepository.findById(courseId).isEmpty()) {
            throw new RuntimeException("Course not found with the given courseId.");
        }

        if (userRepository.findById(studentId).isEmpty()) {
            throw new RuntimeException("Student not found with the given studentId.");
        }

        if (quizRepository.findByCourseIdAndId(courseId, quizId).isEmpty()) {
            throw new RuntimeException("Quiz with ID: " + quizId + " does not exist");
        }

        QuizAttemptResultsDTO results = new QuizAttemptResultsDTO();
        Quiz quiz = quizRepository.findByCourseIdAndId(courseId, quizId).get();
        Optional<QuizAttempt> quizAttempt = quizAttemptRepository.findQuizAttemptByCourseIdAndStudentIdAndQuizId(courseId, studentId, quizId);
        results.setQuiz(quiz);
        results.setAnswers(quizAttempt.get().getAnswers());
        results.setScore(quizAttempt.get().getScore());
        results.setAttemptTime(quizAttempt.get().getAttemptTime());
        return results;
    }


    public List<QuizAttempt> getQuizzesSubmittedByStudent(String studentId, String courseId) {
        return quizAttemptRepository.findQuizAttemptsByStudentIdAndCourseId(studentId, courseId);
    }


    public double getQuizzesGradeByStudent(String studentId, String courseId) {
        return quizAttemptRepository.findQuizAttemptsByStudentIdAndCourseId(studentId, courseId).stream().mapToDouble(QuizAttempt::getScore).sum();
    }
}

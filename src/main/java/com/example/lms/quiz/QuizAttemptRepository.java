package com.example.lms.quiz;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, String> {
    Optional<QuizAttempt> findQuizAttemptByCourseIdAndStudentIdAndQuizId(String courseId, String studentId, String quizId);

    List<QuizAttempt> findQuizAttemptsByStudentIdAndCourseId(String studentId, String courseId);
}

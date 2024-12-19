package com.example.lms.assessment.repository;

import com.example.lms.assessment.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz, String> {

    Optional<Quiz> findByCourseIdAndId(String courseId, String quizId);

    List<Quiz> findByCourseId(String courseId);
}

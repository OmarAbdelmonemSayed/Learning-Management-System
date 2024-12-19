package com.example.lms.assessment.repository;

import com.example.lms.assessment.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, String> {

    List<Question> findByQuizIdAndCourseId(String quizId, String courseId);
    List<Question> findByQuizId(String quizId);
}

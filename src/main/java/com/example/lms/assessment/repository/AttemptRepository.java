package com.example.lms.assessment.repository;

import com.example.lms.assessment.entity.Attempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttemptRepository extends JpaRepository<Attempt, String> {
    List<Attempt> findByStudentIdAndQuizId(String studentId, String quizId);
    Optional<Attempt> findByStudentIdAndQuizIdAndId(String studentId, String quizId, String Id);
}

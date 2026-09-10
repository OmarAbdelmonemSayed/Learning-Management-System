package com.example.lms.submission;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findByAssignmentId(String assignmentId);

    List<Submission> findByAssignmentIdAndStudentId(String assignmentId, String studentId);
}

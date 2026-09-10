package com.example.lms.PerformanceTracking.dto;

import java.util.ArrayList;
import java.util.List;

import com.example.lms.quiz.QuizAttempt;
import com.example.lms.submission.Submission;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StudentPerformanceDTO {

  private String studentId;     
  private String studentName;
  private String courseId;
  private String courseName;
  private int totalAssignments;
  private List<Submission> assignmentsSubmitted = new ArrayList<>();
  private double assignmentsGrade;
  private int totalQuizzes;
  private List<QuizAttempt> quizzesSubmitted = new ArrayList<>();
  private double quizzesGrade;
  private int daysAttended;
  private int DaysAbsent;
  private double attendancePercentage;
  private double totalMarks;
  private String grade;

}

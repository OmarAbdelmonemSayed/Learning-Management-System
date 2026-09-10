package com.example.lms.PerformanceTracking;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.lms.PerformanceTracking.dto.StudentPerformanceDTO;
import com.example.lms.assignment.Assignment;
import com.example.lms.auth.JwtService;
import com.example.lms.common.enums.UserRole;
import com.example.lms.course.Course;
import com.example.lms.course.CourseService;
import com.example.lms.quiz.QuizAttemptService;
import com.example.lms.submission.Submission;
import com.example.lms.submission.SubmissionService;
import com.example.lms.user.User;
import com.example.lms.user.UserRepository;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


@Service
public class PerformanceTrackingService {

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private PerformanceTrackingRepository performanceTrackingRepository;
  @Autowired
  private SubmissionService submissionService;
  @Autowired
  private CourseService courseService;
  @Autowired
  private QuizAttemptService quizAttemptService;
  @Autowired
  private JwtService jwtService;


  private User getUserFromRequest(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      throw new IllegalArgumentException("Token is missing or invalid");
    }

    String token = authHeader.substring(7); // Remove "Bearer " prefix
    String userId = jwtService.extractUsername(token);
    if (userId == null) {
      throw new IllegalArgumentException("Invalid token");
    }

    // Find the user by ID
    User user = userRepository.findById(userId).orElse(null);
    if (user == null) {
      throw new IllegalArgumentException("Invalid Instructor ID");
    }
    return user;
  }

  private List<StudentPerformanceDTO> getStudentPerformanceDTOList(List<Course> courses, String studentId) {
    List<StudentPerformanceDTO> studentPerformance = new ArrayList<>();

    

    for (Course course : courses) {
      StudentPerformanceDTO performance = new StudentPerformanceDTO();
      performance.setStudentId(studentId);
      performance.setStudentName(course.getStudents().stream().filter(s -> s.getId().equals(studentId)).findFirst().get().getName());
      performance.setCourseId(course.getId());
      performance.setCourseName(course.getTitle());
      performance.setTotalAssignments(course.getAssignments().size());
      performance.setTotalQuizzes(course.getQuizzes().size());
      performance.setQuizzesSubmitted(quizAttemptService.getQuizzesSubmittedByStudent(studentId, course.getId()));
      performance.setQuizzesGrade(quizAttemptService.getQuizzesGradeByStudent(studentId, course.getId()));
      performance.setDaysAttended(courseService.getDaysAttendedByStudent(course.getId(), studentId));
      performance.setDaysAbsent(courseService.getDaysAbsentByStudent(course.getId(), studentId));
      performance.setAttendancePercentage(courseService.getAttendancePercentage(course.getId(), studentId));

      // get assignments submitted by student in specific course
      List<Assignment> assignments = course.getAssignments();
      List<Submission> submissions = new ArrayList<>();

      // use assignemtent IDs to get submissions
      for (Assignment assignment : assignments) {
        submissions.addAll(submissionService.getSubmissionsByAssignmentIdAndStudentId(assignment.getId(), studentId));
      }
      performance.setAssignmentsSubmitted(submissions);
    
      double totalGrade = 0;
      for (Submission submission : submissions) {
        totalGrade += submission.getGrade();
      }
      performance.setAssignmentsGrade(totalGrade);

      performance.setTotalMarks(performance.getAssignmentsGrade() + performance.getQuizzesGrade());
      performance.setGrade(performance.getTotalMarks() >= 90 ? "A+" : performance.getTotalMarks() >= 80 ? "B" : performance.getTotalMarks() >= 70 ? "C" : performance.getTotalMarks() >= 60 ? "D" : "F");

      studentPerformance.add(performance);
    }


    return studentPerformance;
  }

  private List<StudentPerformanceDTO> getStudentPerformanceDataByStudent(String studentId)
  {
    // Find the student by ID
    User student = userRepository.findById(studentId).orElse(null);
    if (student == null || !student.getRole().equals(UserRole.STUDENT)) {
        throw new IllegalArgumentException("Invalid Student ID or role");
    }

    // Find the courses associated with the student
    List<Course> courses = performanceTrackingRepository.findCoursesByStudentId(studentId);

    return getStudentPerformanceDTOList(courses, studentId);
  }

  public List<StudentPerformanceDTO> getStudentPerformanceDataByinstructorAndStudent(String instructorId, String studentId) {
    // Find the student by ID
    User student = userRepository.findById(studentId).orElse(null);
    if (student == null || !student.getRole().equals(UserRole.STUDENT)) {
        throw new IllegalArgumentException("Invalid Student ID or role");
    }

    // Find the courses associated with both Instructor and Student
    List<Course> courses = performanceTrackingRepository.findCoursesByInstructorAndStudent(instructorId, studentId);

    return getStudentPerformanceDTOList(courses, studentId);
  }

  private CellStyle createHeaderCellStyle(Workbook workbook) {
    CellStyle style = workbook.createCellStyle();
    Font font = workbook.createFont();
    font.setBold(true);
    style.setFont(font);
    style.setAlignment(HorizontalAlignment.CENTER);
    style.setVerticalAlignment(VerticalAlignment.CENTER);
    return style;
  }


  public ResponseEntity<?> getStudentPerformance(String studentId, HttpServletRequest request) {
    User user = null;
    
    try {
        user = getUserFromRequest(request);
    } catch (IllegalArgumentException e) {
        switch (e.getMessage()) {
            case "Token is missing or invalid":
                return new ResponseEntity<>("Token is missing or invalid", HttpStatus.UNAUTHORIZED);
            case "Invalid token":
                return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
            case "Invalid Instructor ID":
                return new ResponseEntity<>("Invalid Instructor ID", HttpStatus.UNAUTHORIZED);
        }
    }

    if (!user.getRole().equals(UserRole.INSTRUCTOR)) {
        return new ResponseEntity<>("User is not an Instructor", HttpStatus.FORBIDDEN);
    }

    try {
        List<StudentPerformanceDTO> studentPerformance = getStudentPerformanceDataByinstructorAndStudent(user.getId(), studentId);
        return new ResponseEntity<>(studentPerformance, HttpStatus.OK);
    } catch (IllegalArgumentException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }

  public ResponseEntity<?> getStudentReport(String studentId, HttpServletRequest request, HttpServletResponse response) throws java.io.IOException {
    User user = null;
    
    try {
      user = getUserFromRequest(request);
    } catch (IllegalArgumentException e) {
      switch (e.getMessage()) {
        case "Token is missing or invalid":
            return new ResponseEntity<>("Token is missing or invalid", HttpStatus.UNAUTHORIZED);
        case "Invalid token":
            return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
        case "Invalid Instructor ID":
            return new ResponseEntity<>("Invalid Instructor ID", HttpStatus.UNAUTHORIZED);
      }
    }
    
    List<StudentPerformanceDTO> studentPerformance = null;

    if (user.getRole().equals(UserRole.STUDENT)) {
      return new ResponseEntity<>("User is a Student", HttpStatus.FORBIDDEN);
    } else if (user.getRole().equals(UserRole.ADMIN)) {
      studentPerformance = getStudentPerformanceDataByStudent(studentId);
    } else if (user.getRole().equals(UserRole.INSTRUCTOR)) {
      studentPerformance = getStudentPerformanceDataByinstructorAndStudent(user.getId(), studentId);
    } else {
      return new ResponseEntity<>("User role is invalid", HttpStatus.FORBIDDEN);
    }

    if (studentPerformance == null) {
      return new ResponseEntity<>("Student performance data not found", HttpStatus.NOT_FOUND);
    }

    // Generate report
    try (Workbook workbook = new XSSFWorkbook()) {
        Sheet sheet = workbook.createSheet("Student Performance Report");

        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Student ID", "Student Name", "Course ID", "Course Name", "Total Assignments", 
                            "Assignments Submitted", "Assignments Grade", "Total Quizzes", 
                            "Quizzes Submitted", "Quizzes Grade", "Days Attended", "Days Absent", 
                            "Attendance Percentage", "Total Marks", "Grade"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(createHeaderCellStyle(workbook));
        }

        // Populate rows with student performance data
        int rowNum = 1;
        for (StudentPerformanceDTO performance : studentPerformance) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(performance.getStudentId());
            row.createCell(1).setCellValue(performance.getStudentName());
            row.createCell(2).setCellValue(performance.getCourseId());
            row.createCell(3).setCellValue(performance.getCourseName());
            row.createCell(4).setCellValue(performance.getTotalAssignments());
            row.createCell(5).setCellValue(performance.getAssignmentsSubmitted().size());
            row.createCell(6).setCellValue(performance.getAssignmentsGrade());
            row.createCell(7).setCellValue(performance.getTotalQuizzes());
            row.createCell(8).setCellValue(performance.getQuizzesSubmitted().size());
            row.createCell(9).setCellValue(performance.getQuizzesGrade());
            row.createCell(10).setCellValue(performance.getDaysAttended());
            row.createCell(11).setCellValue(performance.getDaysAbsent());
            row.createCell(12).setCellValue(performance.getAttendancePercentage());
            row.createCell(13).setCellValue(performance.getTotalMarks());
            row.createCell(14).setCellValue(performance.getGrade());
        }

        // Auto-size all columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }


        // Write the output to the response
        ServletOutputStream outputStream = response.getOutputStream();
        if (outputStream == null) {
            return new ResponseEntity<>("Error generating the report", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();

        return new ResponseEntity<>("Report generated successfully", HttpStatus.OK);
    } catch (IOException e) {
        return new ResponseEntity<>("Error generating the report", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}

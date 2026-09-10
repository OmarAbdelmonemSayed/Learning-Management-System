package com.example.lms.submission;

import com.example.lms.auth.JwtService;
import com.example.lms.common.enums.UserRole;
import com.example.lms.course.CourseService;
import com.example.lms.user.User;
import com.example.lms.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.lms.course.Course;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/courses/{courseId}/assignments/{assignmentId}/submissions")
public class SubmissionController {

    @Autowired
    private SubmissionService submissionService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService; // Inject JwtService
    @Autowired
    private CourseService courseService;

    @PostMapping
    public ResponseEntity<?> submitAssignmentWithFile(
            @PathVariable String courseId,
            @PathVariable String assignmentId,
            @RequestParam("file") MultipartFile file, // استلام الملف المرفوع
            HttpServletRequest request) {

        // استخراج JWT Token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return new ResponseEntity<>("Token is missing or invalid", HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);
        String userId = jwtService.extractUsername(token);
        if (userId == null) {
            return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
        }

        // العثور على المستخدم
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        if (!user.getRole().equals(UserRole.STUDENT)) {
            return new ResponseEntity<>("User role not supported", HttpStatus.FORBIDDEN);
        }

        Course course = courseService.getCourseById(courseId);
        boolean isUserEnrolled = course.getStudents().stream().anyMatch(student -> student.equals(user));

        if (!isUserEnrolled) {
            return new ResponseEntity<>("User is not enrolled in this course", HttpStatus.FORBIDDEN);
        }

        try {
            // حفظ التقديم
            Submission submission = new Submission();
            submission.setAssignmentId(assignmentId);
            submission.setStudentId(userId);
            submission.setSubmittedDate(LocalDateTime.now());

            // استدعاء الخدمة لحفظ الملف
            Submission createdSubmission = submissionService.submitAssignmentWithFile(submission, file);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSubmission);
        } catch (IOException e) {
            return new ResponseEntity<>("File upload failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 2. عرض جميع التقديمات لواجب معين
    @GetMapping
    public ResponseEntity<List<Submission>> getSubmissionsByAssignmentId(@PathVariable Long courseId, @PathVariable String assignmentId) {
        List<Submission> submissions = submissionService.getSubmissionsByAssignmentId(assignmentId); // جلب جميع التقديمات لواجب معين
        return ResponseEntity.ok(submissions); // إرجاع التقديمات
    }

    @GetMapping("/{submissionId}/view")
    public ResponseEntity<?> viewSubmissionFile(@PathVariable Long submissionId) {
        try {
            // استدعاء الخدمة للحصول على محتوى الملف
            byte[] fileContent = submissionService.getSubmissionFileContent(submissionId);
            Submission submission = submissionService.getSubmissionById(submissionId);

            // تحديد نوع الملف بناءً على الامتداد
            String filePath = submission.getFilePath();
            String contentType = Files.probeContentType(Paths.get(filePath));

            if (contentType == null) {
                contentType = "application/octet-stream"; // النوع الافتراضي
            }

            // إعداد الـ Response
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, contentType) // تعيين نوع الملف
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + Paths.get(filePath).getFileName().toString() + "\"")
                    .body(fileContent);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            return new ResponseEntity<>("Failed to load file: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 3. إضافة ملاحظات على تقديم معين (لـ Instructor فقط)
    @PostMapping("/{submissionId}/feedback")
    public ResponseEntity<?> addFeedback(
            @PathVariable Long submissionId,
            @RequestBody FeedbackDTO feedbackDTO,
            HttpServletRequest request) {

        // 1. Extract JWT Token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return new ResponseEntity<>("Token is missing or invalid", HttpStatus.UNAUTHORIZED);
        }
        String token = authHeader.substring(7);

        // 2. Extract user ID from token
        String userId = jwtService.extractUsername(token);
        if (userId == null) {
            return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
        }

        // 3. Check if user exists
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        // 4. Check if user is an INSTRUCTOR
        if (!user.getRole().equals(UserRole.INSTRUCTOR)) {
            return new ResponseEntity<>("User is not authorized to add feedback", HttpStatus.FORBIDDEN);
        }

        // 5. Add feedback to the submission
        Submission submissionWithFeedback = submissionService.addFeedback(submissionId, feedbackDTO.getFeedback());  // تمرير الـ feedback كـ String
        if (submissionWithFeedback == null) {
            return new ResponseEntity<>("Submission not found", HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(submissionWithFeedback);
    }

    // 4. إضافة الدرجات على واجب معين (لـ Instructor فقط)
    @PostMapping("/{submissionId}/grade")
    public ResponseEntity<?> gradeAssignment(
            @PathVariable Long submissionId,
            @RequestBody GradeDto gradeDTO,  // تعديل هنا ليكون String بدل int
            HttpServletRequest request) {

        // 1. استخراج الـ JWT token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return new ResponseEntity<>("Token is missing or invalid", HttpStatus.UNAUTHORIZED);
        }
        String token = authHeader.substring(7);

        // 2. استخراج ID المستخدم من الـ token
        String userId = jwtService.extractUsername(token);
        if (userId == null) {
            return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
        }

        // 3. التحقق من وجود المستخدم
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        // 4. التحقق من أن المستخدم هو `INSTRUCTOR`
        if (!user.getRole().equals(UserRole.INSTRUCTOR)) {
            return new ResponseEntity<>("User is not authorized to grade assignments", HttpStatus.FORBIDDEN);
        }

        // 5. تعيين الدرجة للتقديم
        Submission gradedSubmission = submissionService.gradeAssignment(submissionId, gradeDTO.getGrade());  // تمرير الـ grade كـ Long
        if (gradedSubmission == null) {
            return new ResponseEntity<>("Submission not found", HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(gradedSubmission);  // إرجاع الـ Submission المعدلة مع الدرجة
    }

}

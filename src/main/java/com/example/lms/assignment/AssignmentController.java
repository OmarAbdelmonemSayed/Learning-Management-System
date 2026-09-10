package com.example.lms.assignment;


import com.example.lms.auth.JwtService;
import com.example.lms.common.enums.UserRole;
import com.example.lms.user.User;
import com.example.lms.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@RestController
@RequestMapping("/courses/{courseId}/assignments")
public class AssignmentController {

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    // 1. إنشاء واجب جديد
    @PostMapping
    public ResponseEntity<?> createAssignment(@PathVariable String courseId,
                                              @RequestBody AssignmentDto assignmentDto,
                                              HttpServletRequest request) {
        // استخراج التوكن من الهيدر
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return new ResponseEntity<>("Token is missing or invalid", HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7); // إزالة "Bearer " من البداية

        // استخراج الـ userId من التوكن
        String userId = jwtService.extractUsername(token);
        if (userId == null) {
            return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
        }

        // جلب المستخدم بناءً على الـ userId
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        // التحقق من دور المستخدم
        if (user.getRole().equals(UserRole.INSTRUCTOR)) {
            // تحويل الـ AssignmentDto إلى Assignment
            Assignment assignment = new Assignment();
            assignment.setTitle(assignmentDto.getTitle());
            assignment.setDescription(assignmentDto.getDescription());
            assignment.setDueDate(assignmentDto.getDueDate());

            // استدعاء الخدمة لإنشاء الواجب
            Assignment createdAssignment = assignmentService.createAssignment(courseId, assignment);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAssignment);
        } else {
            return new ResponseEntity<>("User role not supported", HttpStatus.FORBIDDEN);
        }
    }

    // 2. عرض جميع الواجبات في الكورس
    @GetMapping
    public ResponseEntity<List<Assignment>> getAssignmentsByCourseId(@PathVariable String courseId) {
        List<Assignment> assignments = assignmentService.getAssignmentsByCourseId(courseId);
        return ResponseEntity.ok(assignments);
    }
}


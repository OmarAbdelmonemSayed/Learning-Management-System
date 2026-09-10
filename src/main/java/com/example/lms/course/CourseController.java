package com.example.lms.course;

import com.example.lms.auth.JwtService;
import com.example.lms.lesson.*;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final JwtService jwtService;

    // API for students to view available courses
    @GetMapping("/available")
    @RolesAllowed({"STUDENT", "INSTRUCTOR"})
    public  ResponseEntity<?> getAvailableCourses(HttpServletRequest request) {
    	String userId = extractUserId(request);
        return ResponseEntity.ok(courseService.getAvailableCourses(userId));
    }
    
    // API to get related courses based on user role
    @GetMapping("/related")
    @RolesAllowed({"STUDENT", "INSTRUCTOR"})
    public ResponseEntity<?> getRelatedCourses(HttpServletRequest request) {
    	String userId = extractUserId(request);
        
        return ResponseEntity.ok(courseService.getRelatedCourses(userId));
    }

    
    // API for students to enroll in a course
    @RolesAllowed({"STUDENT"})
    @PostMapping("/{courseId}/enroll")
    public ResponseEntity<?> enrollInCourse(@PathVariable String courseId, HttpServletRequest request) {
    	String userId = extractUserId(request);
        
        courseService.enrollInCourse(courseId, userId);
        
        return ResponseEntity.ok("Enrolled successfully");
    }
    
    // API for instructors to view enrolled students in their courses
    @GetMapping("/{courseId}/students")
    @RolesAllowed({"INSTRUCTOR", "ADMIN"})
    public ResponseEntity<?> getEnrolledStudents(@PathVariable String courseId, HttpServletRequest request) {
    	String userId = extractUserId(request);
        
        return ResponseEntity.ok(courseService.getEnrolledStudents(courseId, userId));
    }
    
    // Endpoint for instructors to create courses
    @PostMapping("/create")
    @RolesAllowed({"INSTRUCTOR"})
    public ResponseEntity<?> createCourse(@RequestBody CourseDto courseDto, HttpServletRequest request) {
    	String userId = extractUserId(request);
        
    	try {
            Course course = courseService.createCourse(courseDto, userId);
            return new ResponseEntity<>(course, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Only instructors can create courses")) {
                return new ResponseEntity<>("Forbidden", HttpStatus.FORBIDDEN);
            }
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);  // Handle other exceptions
        }
    }
    
    // API for instructors to add lessons to their courses
    @PostMapping("/{courseId}/add-lesson")
    @RolesAllowed({"INSTRUCTOR"})
    public ResponseEntity<?> addLesson(@PathVariable String courseId, @RequestBody Lesson lessonRequest, HttpServletRequest request) {
    	String userId = extractUserId(request);
        
    	try {
            courseService.addLesson(courseId, lessonRequest, userId);
            return ResponseEntity.ok("Lesson added successfully");
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(e.getReason(), e.getStatusCode());
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // API to get a course by its ID
    @GetMapping("/{courseId}")
    @RolesAllowed({"STUDENT", "INSTRUCTOR", "ADMIN"})
    public ResponseEntity<?> getCourseById(@PathVariable String courseId, HttpServletRequest request) {
        String userId = extractUserId(request);
        
        try {
            Course course = courseService.getCourseById(courseId, userId);
            return ResponseEntity.ok(course);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    
    // API for deleting a course
    @DeleteMapping("/{courseId}")
    @RolesAllowed({"INSTRUCTOR", "ADMIN"})
    public ResponseEntity<?> deleteCourse(@PathVariable String courseId, HttpServletRequest request) {
        String userId = extractUserId(request);

        try {
            courseService.deleteCourse(courseId, userId);
            return ResponseEntity.ok("Course deleted successfully");
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(e.getReason(), e.getStatusCode());
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    
    private String extractUserId(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        	throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing or invalid token");
        }
        String token = authHeader.substring(7);
        String userId = jwtService.extractUsername(token);

        if (userId == null) {
        	throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing or invalid token");
    	}
        
        return userId;
    }
}
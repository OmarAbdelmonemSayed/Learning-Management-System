package com.example.lms.assignment;

import com.example.lms.course.Course;
import com.example.lms.course.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssignmentService {
    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    public Assignment createAssignment(String courseId, Assignment assignment) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        assignment.setCourse(course);
        return assignmentRepository.save(assignment);
    }

    public List<Assignment> getAssignmentsByCourseId(String courseId) {
        return assignmentRepository.findByCourseId(courseId);
    }
}


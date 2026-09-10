package com.example.lms.PerformanceTracking;

import com.example.lms.course.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PerformanceTrackingRepository extends JpaRepository<Course, String> {

    // Find all courses taught by an instructor
    List<Course> findByInstructorId(String instructorId);

    // Find all courses a student is enrolled in
    @Query("SELECT c FROM Course c JOIN c.students s WHERE s.id = :studentId")
    List<Course> findCoursesByStudentId(String studentId);

    // Find all courses where a student is enrolled and the instructor teaches
    @Query("SELECT c FROM Course c JOIN c.students s WHERE c.instructor.id = :instructorId AND s.id = :studentId")
    List<Course> findCoursesByInstructorAndStudent(String instructorId, String studentId);

}

package com.example.lms.course;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.lms.common.enums.UserRole;
import com.example.lms.lesson.Lesson;
import com.example.lms.lesson.LessonRepository;
import com.example.lms.user.User;
import com.example.lms.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;
    
    public List<Course> getAvailableCourses(String userId) {
    	User user = validateUser(userId);
    	
        List<Course> courses = courseRepository.findAll();

        if (user.getRole().equals(UserRole.STUDENT)) {
            courses.forEach(course -> {
                course.setStudents(null);
                course.setInstructor(null);
                course.getLessons().forEach(lesson -> lesson.setOtp(null));
                course.getLessons().forEach(lesson -> lesson.setStudentsAttended(null));
            });
        }
        return courses;
    }

    public List<Course> getRelatedCourses(String userId) {
    	User user = validateUser(userId);
        
        List<Course> relatedCourses;

        if (user.getRole().equals(UserRole.INSTRUCTOR)) {
            relatedCourses = courseRepository.findAll().stream()
                    .filter(course -> course.getInstructor().equals(user))
                    .toList();
        } 
        
        else if (user.getRole().equals(UserRole.STUDENT)) {
            relatedCourses = courseRepository.findAll().stream()
                    .filter(course -> course.getStudents().contains(user))
                    .toList();
            relatedCourses.forEach(course -> {
                course.setStudents(null);
                course.setInstructor(null);
                course.getLessons().forEach(lesson -> lesson.setOtp(null));
                course.getLessons().forEach(lesson -> lesson.setStudentsAttended(null));
            });
        } 
        
        else {
            throw new RuntimeException("User role not supported");
        }
        return relatedCourses;
    }
    
    public void enrollInCourse(String courseId, String userId) {
    	User student = validateUser(userId);
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!student.getRole().equals(UserRole.STUDENT)) {
            throw new RuntimeException("Only students can enroll in courses.");
        }
        
        if (course.getStudents().contains(student)) {
            throw new RuntimeException("Student is already enrolled in this course.");
        }

        course.getStudents().add(student);
        courseRepository.save(course);
    }

    public List<User> getEnrolledStudents(String courseId, String userId) {
    	User user = validateUser(userId);
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.getInstructor().equals(user) && !user.getRole().equals(UserRole.ADMIN)) {
            throw new RuntimeException("Unauthorized access");
        }
        return course.getStudents();
    }

    public Course createCourse(CourseDto courseDto, String userId) {
    	User instructor = validateUser(userId);
        
    	if (!instructor.getRole().equals(UserRole.INSTRUCTOR)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only instructors can create courses.");
        }

        Course course = new Course();
        course.setTitle(courseDto.getTitle());
        course.setDescription(courseDto.getDescription());
        course.setDuration(courseDto.getDuration());
        course.setInstructor(instructor);
        
        instructor.getCourses().add(course);
        userRepository.save(instructor);

        return courseRepository.save(course);
    }
    
    public void addLesson(String courseId, Lesson lessonRequest, String userId) {
    	User instructor = validateUser(userId);
        
    	Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));

        if (!course.getInstructor().equals(instructor)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized access");
        }

        Lesson lesson = new Lesson();
        lesson.setName(lessonRequest.getName());
        lesson.setCourse(course);
        lessonRepository.save(lesson);
        
        course.getLessons().add(lesson);
        courseRepository.save(course);
    }
    
    public Course getCourseById(String courseId, String userId) {
        User user = validateUser(userId);
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (user.getRole().equals(UserRole.STUDENT)) {
            course.setInstructor(null);
            course.setStudents(null);
            course.getLessons().forEach(lesson -> lesson.setOtp(null));
            course.getLessons().forEach(lesson -> lesson.setStudentsAttended(null));
        }
        return course;
    }
    
    public void deleteCourse(String courseId, String userId) {
        User user = validateUser(userId);

        Course course = courseRepository.findById(courseId)
                		.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));

        if (!course.getInstructor().equals(user) && !user.getRole().equals(UserRole.ADMIN)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized access");
        }

        courseRepository.delete(course);
    }

    
    private User validateUser(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
    
    // For performance
    public int getDaysAttendedByStudent(String courseId, String studentId) {
        User student = validateUser(studentId);
        if (!student.getRole().equals(UserRole.STUDENT)) {
            throw new RuntimeException("Only students can have attendance checked.");
        }

        Course course = courseRepository.findById(courseId)
                		.orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.getStudents().contains(student)) {
            throw new RuntimeException("Student is not enrolled in the course.");
        }

        long attendanceCount = course.getLessons().stream()
                .filter(lesson -> lesson.getStudentsAttended().contains(student))
                .count();

        return (int) attendanceCount;
    }
    
    public int getDaysAbsentByStudent(String courseId, String studentId) {
        User student = validateUser(studentId);
        if (!student.getRole().equals(UserRole.STUDENT)) {
            throw new RuntimeException("Only students can have attendance checked.");
        }

        Course course = courseRepository.findById(courseId)
                		.orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.getStudents().contains(student)) {
            throw new RuntimeException("Student is not enrolled in the course.");
        }

        long totalLessons = course.getLessons().size();

        long attendedLessons = course.getLessons().stream()
                .filter(lesson -> lesson.getStudentsAttended().contains(student))
                .count();

        long absentLessons = totalLessons - attendedLessons;

        return (int) absentLessons;
    }

    public double getAttendancePercentage(String courseId, String studentId) {
        User student = validateUser(studentId);
        if (!student.getRole().equals(UserRole.STUDENT)) {
            throw new RuntimeException("Only students can have attendance checked.");
        }

        Course course = courseRepository.findById(courseId)
                		.orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.getStudents().contains(student)) {
            throw new RuntimeException("Student is not enrolled in the course.");
        }

        long totalLessons = course.getLessons().size();

        long attendedLessons = course.getLessons().stream()
                .filter(lesson -> lesson.getStudentsAttended().contains(student))
                .count();

        return (double) attendedLessons / totalLessons * 100;
    }
    
    // for quizzes
    public Course getCourseById(String courseId) {
        Course course = courseRepository.findById(courseId)
        				.orElseThrow(() -> new RuntimeException("Course not found"));
        return course;
    }
}

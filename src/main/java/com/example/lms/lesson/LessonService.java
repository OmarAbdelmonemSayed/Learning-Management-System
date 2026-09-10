package com.example.lms.lesson;

import com.example.lms.user.User;
import com.example.lms.user.UserRepository;
import com.example.lms.common.enums.UserRole;
import com.example.lms.course.*;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    // Create a lesson for a course
    public Lesson createLesson(LessonDto createLessonDto, String instructorId) {
    	User instructor = validateUser(instructorId, UserRole.INSTRUCTOR);
    	
        Course course = courseRepository.findById(createLessonDto.getCourseId())
                		.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));

        if (!course.getInstructor().equals(instructor)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the instructor of this course");
        }

        Lesson lesson = Lesson.builder()
                .name(createLessonDto.getName())
                .course(course)
                .build();

        return lessonRepository.save(lesson);
    }

    public Lesson generateOtp(String lessonId, String instructorId) {
    	User instructor = validateUser(instructorId, UserRole.INSTRUCTOR);
    	
        Lesson lesson = lessonRepository.findById(lessonId)
                		.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found"));

        if (!lesson.getCourse().getInstructor().equals(instructor)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the instructor of this course");
        }

        String otp = String.valueOf((int) (Math.random() * 9000) + 1000); // Generate 4-digit OTP
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(10); // OTP valid for 10 minutes
        lesson.setOtp(otp);
        lesson.setOtpExpirationTime(expirationTime);

        return lessonRepository.save(lesson);
    }
    
    public void attendLesson(String lessonId, String otp, String studentId) {
    	User student = validateUser(studentId, UserRole.STUDENT);
    	
        Lesson lesson = lessonRepository.findById(lessonId)
                		.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found"));

        if (lesson.getOtpExpirationTime() == null || LocalDateTime.now().isAfter(lesson.getOtpExpirationTime())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP expired");
        }
        
        if (!lesson.getOtp().equals(otp)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid OTP");
        }

        Course course = lesson.getCourse();
        if (!course.getStudents().contains(student)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not enrolled in this course");
        }

        if (lesson.getStudentsAttended().contains(student)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Attendance already marked for this lesson");
        }

        lesson.getStudentsAttended().add(student);
        lessonRepository.save(lesson);
    }

    private User validateUser(String userId, UserRole requiredRole) {
        User user = userRepository.findById(userId)
                	.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!user.getRole().equals(requiredRole)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have the required role");
        }
        return user;
    }
}

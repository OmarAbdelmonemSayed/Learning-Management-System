package com.example.lms.course;

import com.example.lms.question.Question;
import com.example.lms.quiz.Quiz;
import com.example.lms.user.User;
import com.example.lms.assignment.Assignment;
import com.example.lms.lesson.Lesson;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courses")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private int duration;  // in hours or minutes

    // One-to-Many: An instructor can teach many courses
    @ManyToOne
    @JoinColumn(name = "instructor_id", nullable = false)
    private User instructor;

    // Many-to-Many: A student can enroll in many courses, and a course can have many students
    @ManyToMany
    @JoinTable(
            name = "course_students",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private List<User> students = new ArrayList<>();

    // One-to-Many: A course can have many lessons
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Lesson> lessons = new ArrayList<>();

    // One-to-Many: A course can have many assignments
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Assignment> assignments = new ArrayList<>();

//     One-to-Many: A course can have many quizzes
    @OneToMany
    @JoinTable(
            name = "course_quizzes",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "quiz_id")
    )
    private List<Quiz> quizzes = new ArrayList<>();


    //     One-to-Many: A course can have many questions (question-bank)
    @OneToMany
    @JoinTable(
            name = "course_questions",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "question_id")
    )
    private List<Question> questions;
}
package com.example.lms.assessment.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Attempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(name = "student_id", nullable = false)
    private String studentId;

    @Column(name = "quiz_id", nullable = false)
    private String quizId;

    @ElementCollection
    @CollectionTable(name = "answers", joinColumns = @JoinColumn(name = "attempt_id"))
    @Column(name = "answer")
    private List<String> answers;

    @Column(name = "attempt_time", nullable = false)
    private long attemptTime;

    @Column(name = "score", nullable = false)
    private int score;

    public Attempt() {}

    public Attempt(String studentId, String quizId, List<String> answers, long attemptTime, int score) {
        this.studentId = studentId;
        this.quizId = quizId;
        this.answers = answers;
        this.attemptTime = attemptTime;
        this.score = score;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    public long getAttemptTime() {
        return attemptTime;
    }

    public void setAttemptTime(long attemptTime) {
        this.attemptTime = attemptTime;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}

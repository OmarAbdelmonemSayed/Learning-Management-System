package com.example.lms.assessment.dto;

import jakarta.validation.constraints.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AttemptDTO {

    private String attemptId;
    private List<String> answers;
    private int score;
    private String attemptTime;

    public String getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(String attemptId) {
        this.attemptId = attemptId;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getAttemptTime() {
        return attemptTime;
    }

    public void setAttemptTime(long attemptTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.attemptTime = sdf.format(new Date(attemptTime));
    }
}

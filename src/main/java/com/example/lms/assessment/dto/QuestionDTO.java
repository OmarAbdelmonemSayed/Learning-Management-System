package com.example.lms.assessment.dto;

import jakarta.validation.constraints.*;

import java.util.List;

public class QuestionDTO {

    private String id;
    private String courseId;
    private String quizId;

    @NotNull(message = "Question text cannot be null")
    @Size(min = 1, message = "Question text must not be empty")
    private String questionText;

    @NotNull(message = "Question type cannot be null")
    @Pattern(regexp = "^(TRUE/FALSE|MCQ|Short Answer)$", message = "Question type must be either TRUE/FALSE or MCQ or Short Answer")
    private String questionType;
    private Boolean correctAnswer;
    private List<String> choices;
    @Min(value = 0, message = "Correct choice index must be a positive number")
    private Integer correctChoiceIndex;

    @Max(value = 50, message = "Short Answer must not exceed 50 characters")
    private String shortAnswer;


    public QuestionDTO() {}

    public QuestionDTO(String id, String courseId, String quizId, String questionText, String questionType,
                       Boolean correctAnswer, List<String> choices, Integer correctChoiceIndex, String shortAnswer) {
        this.id = id;
        this.courseId = courseId;
        this.quizId = quizId;
        this.questionText = questionText;
        this.questionType = questionType;
        this.correctAnswer = correctAnswer;
        this.choices = choices;
        this.shortAnswer = shortAnswer;
        this.correctChoiceIndex = correctChoiceIndex;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public Boolean getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(Boolean correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public List<String> getChoices() {
        return choices;
    }

    public void setChoices(List<String> choices) {
        this.choices = choices;
    }

    public Integer getCorrectChoiceIndex() {
        return correctChoiceIndex;
    }

    public void setCorrectChoiceIndex(Integer correctChoiceIndex) {
        this.correctChoiceIndex = correctChoiceIndex;
    }

    public String getShortAnswer() {
        return shortAnswer;
    }

    public void setShortAnswer(String shortAnswer) {
        this.shortAnswer = shortAnswer;
    }
}

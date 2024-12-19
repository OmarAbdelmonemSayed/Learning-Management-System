package com.example.lms.assessment.dto;

import jakarta.validation.constraints.*;

public class QuizDTO {

    private String id;

    @NotNull(message = "Course ID cannot be null")
    private String courseId;

    @NotNull(message = "Title cannot be null")
    @Size(min = 1, max = 100, message = "Title should be between 1 and 100 characters")
    private String title;
    private String description;


    public QuizDTO() {}


    public QuizDTO(String id, String courseId, String title, String description) {
        this.id = id;
        this.courseId = courseId;
        this.title = title;
        this.description = description;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "QuizDTO{id=" + id + ", courseId='" + courseId + "', title='" + title + "', description='" + description + "'}";
    }
}

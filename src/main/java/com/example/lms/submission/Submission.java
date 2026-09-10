package com.example.lms.submission;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String assignmentId;
    private String studentId;
    private String filePath;
    private Long grade;
    private String feedback;
    private LocalDateTime submittedDate;

}

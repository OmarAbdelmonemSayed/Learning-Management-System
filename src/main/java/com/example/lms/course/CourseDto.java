package com.example.lms.course;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDto {
    private String title;
    private String description;
    private int duration;
}
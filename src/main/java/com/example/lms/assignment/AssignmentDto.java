package com.example.lms.assignment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class AssignmentDto {
    private String title;
    private String description;
    private LocalDate dueDate;
}

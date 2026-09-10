package com.example.lms.media;

import com.example.lms.lesson.Lesson;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "media")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(nullable = false)
    private String filename; // Name of the file stored in the media folder

    @Column(nullable = false)
    private String fileType; // Type of the file

    @Column(nullable = false)
    private String filePath; // Full path to the stored file

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;
}

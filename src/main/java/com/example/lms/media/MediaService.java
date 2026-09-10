package com.example.lms.media;

import com.example.lms.common.enums.UserRole;
import com.example.lms.lesson.Lesson;
import com.example.lms.lesson.LessonRepository;
import com.example.lms.user.User;
import com.example.lms.user.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MediaService {

    private final MediaRepository mediaRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;

    @Value("${media.folder}")
    private String mediaFolder;

    public Media uploadMedia(String lessonId, MultipartFile file, String instructorId) throws IOException {
    	validateInstructor(instructorId);
    	
        Lesson lesson = lessonRepository.findById(lessonId)
        				.orElseThrow(() -> new RuntimeException("Lesson not found"));

        File folder = new File(mediaFolder);
        if (!folder.exists()) {
            boolean created = folder.mkdirs();
            if (!created) {
                throw new IOException("Failed to create media folder");
            }
        }

        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path destinationPath = Paths.get(folder.getAbsolutePath(), filename);

        try {
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationPath);
            }
            System.out.println("File uploaded to: " + destinationPath.toString());
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Failed to copy file: " + e.getMessage(), e);
        }

        Media media = Media.builder()
                .filename(filename)
                .fileType(file.getContentType())
                .filePath(destinationPath.toString())
                .lesson(lesson)
                .build();

        return mediaRepository.save(media);
    }

    public List<Media> getMediaByLesson(String lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                		.orElseThrow(() -> new RuntimeException("Lesson not found"));
        return lesson.getMediaList();
    }
    
    public Media getMediaById(String mediaId) {
        return mediaRepository.findById(mediaId)
                .orElseThrow(() -> new RuntimeException("Media not found"));
    }

    public byte[] getMediaContent(String mediaId) throws IOException {
        Media media = getMediaById(mediaId);

        Path path = Paths.get(media.getFilePath());
        if (!Files.exists(path)) {
            throw new RuntimeException("Media file not found on the server");
        }

        return Files.readAllBytes(path);
    }
    
    private void validateInstructor(String instructorId) {
        User user = userRepository.findById(instructorId)
                .orElseThrow(() -> new RuntimeException("Invalid instructor ID"));
        if (!user.getRole().equals(UserRole.INSTRUCTOR)) {
            throw new RuntimeException("Unauthorized access");
        }
    }
}

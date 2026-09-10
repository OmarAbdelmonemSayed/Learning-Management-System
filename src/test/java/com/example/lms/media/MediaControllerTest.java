package com.example.lms.media;

import com.example.lms.auth.JwtService;

import io.jsonwebtoken.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MediaControllerTest {

    @Mock
    private MediaService mediaService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private MediaController mediaController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mediaController).build();
    }

    @Test
    void testUploadMedia_Success() throws Exception {
        String lessonId = "lesson123";
        String instructorId = "instructor123";

        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "File content".getBytes()
        );

        Media media = Media.builder()
                .id("media123")
                .filename("test.txt")
                .fileType("text/plain")
                .filePath("/media/test.txt")
                .build();

        when(jwtService.extractUsername(anyString())).thenReturn(instructorId);
        when(mediaService.uploadMedia(eq(lessonId), any(MultipartFile.class), eq(instructorId))).thenReturn(media);

        mockMvc.perform(multipart("/media/{lessonId}/upload", lessonId)
                        .file(file)
                        .header("Authorization", "Bearer validToken"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("media123"))
                .andExpect(jsonPath("$.filename").value("test.txt"))
                .andExpect(jsonPath("$.fileType").value("text/plain"))
                .andExpect(jsonPath("$.filePath").value("/media/test.txt"));

        verify(mediaService, times(1)).uploadMedia(eq(lessonId), any(MultipartFile.class), eq(instructorId));
    }

    @Test
    void testGetMediaByLesson_Success() throws Exception {
        String lessonId = "lesson123";

        Media media = Media.builder()
                .id("media123")
                .filename("test.txt")
                .fileType("text/plain")
                .filePath("/media/test.txt")
                .build();

        when(mediaService.getMediaByLesson(lessonId)).thenReturn(Collections.singletonList(media));

        mockMvc.perform(get("/media/{lessonId}", lessonId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("media123"))
                .andExpect(jsonPath("$[0].filename").value("test.txt"))
                .andExpect(jsonPath("$[0].fileType").value("text/plain"))
                .andExpect(jsonPath("$[0].filePath").value("/media/test.txt"));

        verify(mediaService, times(1)).getMediaByLesson(lessonId);
    }

    @Test
    void testGetMediaByLesson_LessonNotFound() throws Exception {
        String lessonId = "invalidLessonId";

        when(mediaService.getMediaByLesson(lessonId)).thenThrow(new RuntimeException("Lesson not found"));

        mockMvc.perform(get("/media/{lessonId}", lessonId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Lesson not found"));

        verify(mediaService, times(1)).getMediaByLesson(lessonId);
    }
    
    @Test
    void testViewMedia_Success() throws Exception {
        String mediaId = "media123";

        Media media = Media.builder()
                .id(mediaId)
                .filename("test.txt")
                .fileType("text/plain")
                .filePath("/media/test.txt")
                .build();

        byte[] mediaContent = "File content".getBytes();

        when(mediaService.getMediaById(mediaId)).thenReturn(media);
        when(mediaService.getMediaContent(mediaId)).thenReturn(mediaContent);

        mockMvc.perform(get("/media/{mediaId}/view", mediaId))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "text/plain"))
                .andExpect(content().bytes(mediaContent));

        verify(mediaService, times(1)).getMediaById(mediaId);
        verify(mediaService, times(1)).getMediaContent(mediaId);
    }
}

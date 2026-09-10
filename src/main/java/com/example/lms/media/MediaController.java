package com.example.lms.media;

import com.example.lms.auth.JwtService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;
    private final JwtService jwtService;

    @PostMapping("/{lessonId}/upload")
    @RolesAllowed({"INSTRUCTOR"})
    public ResponseEntity<?> uploadMedia(
            @PathVariable String lessonId,
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request
    ) {
        try {
        	String instructorId = getInstructorIdFromToken(request);
            Media media = mediaService.uploadMedia(lessonId, file, instructorId);
            return new ResponseEntity<>(media, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            return new ResponseEntity<>("File upload failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{lessonId}")
    @RolesAllowed({"STUDENT", "INSTRUCTOR"})
    public ResponseEntity<?> getMediaByLesson(@PathVariable String lessonId) {
    	try {
            return ResponseEntity.ok(mediaService.getMediaByLesson(lessonId));
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/{mediaId}/view")
    @RolesAllowed({"STUDENT", "INSTRUCTOR"})
    public ResponseEntity<?> viewMedia(@PathVariable String mediaId) {
        try {
            byte[] mediaContent = mediaService.getMediaContent(mediaId);
            Media media = mediaService.getMediaById(mediaId);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, media.getFileType())
                    .body(mediaContent);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            return new ResponseEntity<>("Failed to load media: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String getInstructorIdFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token is missing or invalid");
        }

        String token = authHeader.substring(7);
        String userId = jwtService.extractUsername(token);
        
        return userId;
    }
}

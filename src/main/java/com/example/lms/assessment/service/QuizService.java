package com.example.lms.assessment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.lms.assessment.dto.QuizDTO;
import com.example.lms.assessment.entity.Quiz;
import com.example.lms.assessment.repository.QuizRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    public void createQuiz(QuizDTO quizDTO) {
        Quiz quiz = new Quiz();
        quiz.setCourseId(quizDTO.getCourseId());
        quiz.setTitle(quizDTO.getTitle());
        quiz.setDescription(quizDTO.getDescription());
        quizRepository.save(quiz);
    }

    public Optional<QuizDTO> getQuizByCourseAndId(String courseId, String quizId) {
        Optional<Quiz> quiz = quizRepository.findByCourseIdAndId(courseId, quizId);
        return quiz.map(this::mapToDTO);
    }


    public List<QuizDTO> getAllQuizzesByCourse(String courseId) {
        List<Quiz> quizzes = quizRepository.findByCourseId(courseId);
        return quizzes.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public void deleteQuiz(String courseId, String quizId) {
        quizRepository.findByCourseIdAndId(courseId, quizId)
                .ifPresent(quiz -> quizRepository.delete(quiz));
    }

    public boolean existsById(String courseId, String quizId) {
        return quizRepository.findByCourseIdAndId(courseId, quizId).isPresent();
    }

    public void updateQuiz(String courseId, String quizId, QuizDTO quizDTO) {
        Optional<Quiz> existingQuiz = quizRepository.findByCourseIdAndId(courseId, quizId);
        if (existingQuiz.isPresent()) {
            Quiz quiz = existingQuiz.get();
            quiz.setTitle(quizDTO.getTitle());
            quiz.setDescription(quizDTO.getDescription());
            quizRepository.save(quiz);
        }
    }

    private QuizDTO mapToDTO(Quiz quiz) {
        QuizDTO dto = new QuizDTO();
        dto.setId(quiz.getId());
        dto.setCourseId(quiz.getCourseId());
        dto.setTitle(quiz.getTitle());
        dto.setDescription(quiz.getDescription());
        return dto;
    }
}

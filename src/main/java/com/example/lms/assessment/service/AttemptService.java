package com.example.lms.assessment.service;

import com.example.lms.assessment.dto.AttemptDTO;
import com.example.lms.assessment.entity.Attempt;
import com.example.lms.assessment.entity.Question;
import com.example.lms.assessment.repository.AttemptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AttemptService {

    @Autowired
    private AttemptRepository attemptRepository;

    @Autowired
    private QuestionService questionService;

    public Integer submitAttempt(String studentId, String quizId, AttemptDTO attemptDTO) {
        Attempt attempt = new Attempt();
        attempt.setStudentId(studentId);
        attempt.setQuizId(quizId);
        attempt.setAnswers(attemptDTO.getAnswers());
        attempt.setAttemptTime(System.currentTimeMillis());

        List<String> answers = attemptDTO.getAnswers();
        List<String> correctAnswers = questionService.getCorrectAnswersByQuiz(quizId);
        int score = calculateScore(answers, correctAnswers);
        attempt.setScore(score);
        attemptRepository.save(attempt);
        return attempt.getScore();
    }

    public AttemptDTO getAttemptResult(String studentId, String quizId, String attemptId) {
        Attempt attempt = attemptRepository.findById(attemptId).orElse(null);

        if (attempt == null || !attempt.getStudentId().equals(studentId) || !attempt.getQuizId().equals(quizId)) {
            return null;
        }

        AttemptDTO attemptDTO = new AttemptDTO();
        attemptDTO.setAttemptId(attempt.getId());
        attemptDTO.setAnswers(attempt.getAnswers());
        attemptDTO.setScore(attempt.getScore());
        attemptDTO.setAttemptTime(attempt.getAttemptTime());

        return attemptDTO;
    }

    public List<AttemptDTO> getAllAttemptResults(String studentId, String quizId) {
        List<Attempt> attempts = attemptRepository.findByStudentIdAndQuizId(studentId, quizId);

        return attempts.stream().map(attempt -> {
            List<String> answers = attempt.getAnswers();
            List<String> correctAnswers = questionService.getCorrectAnswersByQuiz(quizId);
            int score = calculateScore(answers, correctAnswers);

            AttemptDTO attemptDTO = new AttemptDTO();
            attemptDTO.setAttemptId(attempt.getId());
            attemptDTO.setAnswers(answers);
            attemptDTO.setScore(score);
            attemptDTO.setAttemptTime(attempt.getAttemptTime());

            return attemptDTO;
        }).collect(Collectors.toList());
    }

    public boolean existsById(String studentId, String quizId, String attemptId) {
        Optional<Attempt> attempt = attemptRepository.findByStudentIdAndQuizIdAndId(studentId, quizId, attemptId);
        return attempt.isPresent() && attempt.get().getQuizId().equals(quizId) &&
                attempt.get().getStudentId().equals(studentId) &&
                attempt.get().getId().equals(attemptId);
    }

    public void deleteAttempt(String studentId, String quizId, String attemptId) {
        Optional<Attempt> attempt = attemptRepository.findByStudentIdAndQuizIdAndId(studentId, quizId, attemptId);
        attemptRepository.delete(attempt.get());
    }

    private int calculateScore(List<String> answers, List<String> correctAnswers) {
        int score = 0;

        for (int i = 0; i < answers.size(); i++) {
            if (answers.get(i).equals(correctAnswers.get(i))) {
                score++;
            }
        }

        return score;
    }
}

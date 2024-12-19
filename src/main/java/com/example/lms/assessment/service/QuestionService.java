package com.example.lms.assessment.service;

import com.example.lms.assessment.dto.QuestionDTO;
import com.example.lms.assessment.entity.Question;
import com.example.lms.assessment.repository.QuestionRepository;
import com.example.lms.assessment.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuizRepository quizRepository;


    public void createQuestion(QuestionDTO questionDTO) {
        Question question = new Question(
                questionDTO.getCourseId(),
                questionDTO.getQuizId(),
                questionDTO.getQuestionText(),
                questionDTO.getQuestionType(),
                questionDTO.getCorrectAnswer(),
                questionDTO.getChoices(),
                questionDTO.getCorrectChoiceIndex()
        );
        questionRepository.save(question);
    }

    public QuestionDTO getQuestionByQuizAndId(String courseId, String quizId, String questionId) {
        Optional<Question> question = questionRepository.findById(questionId);

        if (question.isPresent() && question.get().getQuizId().equals(quizId) && question.get().getCourseId().equals(courseId)) {
            return new QuestionDTO(
                    question.get().getId(),
                    question.get().getCourseId(),
                    question.get().getQuizId(),
                    question.get().getQuestionText(),
                    question.get().getQuestionType(),
                    question.get().getCorrectAnswer(),
                    question.get().getChoices(),
                    question.get().getCorrectChoiceIndex()
            );
        } else {
            throw new IllegalArgumentException("Question not found with the given quizId and questionId.");
        }
    }

    public List<String> getCorrectAnswersByQuiz(String quizId) {
        List<Question> questions = questionRepository.findByQuizId(quizId);

        return questions.stream()
                .map(this::getCorrectAnswer)
                .collect(Collectors.toList());
    }

    private String getCorrectAnswer(Question question) {
        if (question.getQuestionType().equals("TRUE/FALSE")) {
            return question.getCorrectAnswer() ? "true" : "false";
        } else if (question.getQuestionType().equals("MCQ")) {
            return question.getChoices().get(question.getCorrectChoiceIndex());
        }
        return "";
    }

    public List<QuestionDTO> getAllQuestionsByQuiz(String courseId, String quizId) {
        List<Question> questions = questionRepository.findByQuizIdAndCourseId(quizId, courseId);
        return questions.stream().map(question -> new QuestionDTO(
                question.getId(),
                question.getCourseId(),
                question.getQuizId(),
                question.getQuestionText(),
                question.getQuestionType(),
                question.getCorrectAnswer(),
                question.getChoices(),
                question.getCorrectChoiceIndex()
        )).collect(Collectors.toList());
    }

    public void updateQuestion(String questionId, QuestionDTO questionDTO) {
        Optional<Question> existingQuestion = questionRepository.findById(questionId);

        Question question = existingQuestion.get();
        question.setQuestionText(questionDTO.getQuestionText());
        question.setQuestionType(questionDTO.getQuestionType());
        question.setCorrectAnswer(questionDTO.getCorrectAnswer());
        question.setChoices(questionDTO.getChoices());
        question.setCorrectChoiceIndex(questionDTO.getCorrectChoiceIndex());
        questionRepository.save(question);

    }

    public void deleteQuestion(String questionId) {
        Optional<Question> question = questionRepository.findById(questionId);
        questionRepository.delete(question.get());
    }

    public boolean existsById(String courseId, String quizId, String questionId) {
        Optional<Question> question = questionRepository.findById(questionId);
        return question.isPresent() && question.get().getQuizId().equals(quizId) && question.get().getCourseId().equals(courseId);
    }
}

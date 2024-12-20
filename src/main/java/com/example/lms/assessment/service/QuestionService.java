package com.example.lms.assessment.service;

import com.example.lms.assessment.dto.QuestionDTO;
import com.example.lms.assessment.entity.Question;
import com.example.lms.assessment.repository.QuestionRepository;
import com.example.lms.assessment.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
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
                questionDTO.getCorrectChoiceIndex(),
                questionDTO.getShortAnswer()
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
                    question.get().getCorrectChoiceIndex(),
                    question.get().getShortAnswer()
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
        else if (question.getQuestionType().equals("Short Answer")){
            return question.getShortAnswer();
        }
        return "";
    }

    public List<QuestionDTO> getAllQuestionsByQuiz(String courseId, String quizId) {
        List<Question> questions = questionRepository.findByQuizIdAndCourseId(quizId, courseId);

//        Collections.shuffle(questions); // randomize the questions order everytime try to get the questions


        return questions.stream()
                .map(question -> {
                    QuestionDTO dto = new QuestionDTO();
                    dto.setId(question.getId());
                    dto.setCourseId(question.getCourseId());
                    dto.setQuizId(question.getQuizId());
                    dto.setQuestionText(question.getQuestionText());
                    dto.setQuestionType(question.getQuestionType());
                    dto.setId(question.getId());

                    if ("MCQ".equals(question.getQuestionType())) {
                        dto.setChoices(question.getChoices());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
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

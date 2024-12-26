package com.berghella.daniele.edu_hub.service;

import com.berghella.daniele.edu_hub.dao.QuestionDAO;
import com.berghella.daniele.edu_hub.model.Question;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class QuestionService {
    private QuestionDAO questionDAO = new QuestionDAO();

    public void createQuestion(Question question){
        questionDAO.createQuestion(question);
    }

    public List<Question> getAllQuestions() {
        return questionDAO.getAllQuestions();
    }

    public List<Question> getQuestionsByTestId(UUID testId) {
        return questionDAO.getQuestionsByTestId(testId);
    }

    public Optional<Question> getQuestionById(UUID id) {
        return questionDAO.getQuestionById(id);
    }

    public Question updateQuestionById(Question questionUpdate, UUID oldQuestionId) {
        return questionDAO.updateQuestionById(questionUpdate, oldQuestionId);
    }

    public boolean isDeletedQuestionById(UUID id) {
        return questionDAO.isDeletedQuestionById(id);
    }
}

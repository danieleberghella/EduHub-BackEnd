package com.berghella.daniele.edu_hub.service;

import com.berghella.daniele.edu_hub.dao.AnswerDAO;
import com.berghella.daniele.edu_hub.model.Answer;

import java.util.List;
import java.util.UUID;

public class AnswerService {
    private AnswerDAO answerDAO = new AnswerDAO();

    public void createAnswer(Answer answer){
        answerDAO.createAnswer(answer);
    }

    public List<Answer> getAnswersByQuestionId(UUID id) {
        return answerDAO.getAnswersByQuestionId(id);
    }

    public boolean isDeletedAnswerById(UUID id) {
        return answerDAO.isDeletedAnswerById(id);
    }
}

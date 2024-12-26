package com.berghella.daniele.edu_hub.dao;

import com.berghella.daniele.edu_hub.model.Answer;
import com.berghella.daniele.edu_hub.model.Question;
import com.berghella.daniele.edu_hub.utility.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AnswerDAO {
    private final Connection connection = DatabaseConnection.getInstance().getConnection();

    public void createAnswer(Answer answer){
        String insertAnswerSQL = "INSERT INTO answer(id, question_id, answer_text, is_correct) " + "VALUES (?, ?, ?, ?);";
        try {
            PreparedStatement psInsertAnswer = connection.prepareStatement(insertAnswerSQL);
            psInsertAnswer.setObject(1, answer.getId());
            psInsertAnswer.setObject(2, answer.getQuestion().getId());
            psInsertAnswer.setString(3, answer.getText());
            psInsertAnswer.setBoolean(4, answer.isCorrectAnswer());
            psInsertAnswer.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Answer> getAnswersByQuestionId(UUID questionId) {
        String query = """
        SELECT 
            a.id AS answer_id,
            a.answer_text AS answer_text,
            a.is_correct,
            q.id AS question_id,
            q.question_text,
            q.points
        FROM 
            answer a
        JOIN 
            question q
        ON 
            a.question_id = q.id
        WHERE 
            a.question_id = ?
    """;

        List<Answer> answers = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setObject(1, questionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Question question = new Question();
                    question.setId(UUID.fromString(rs.getString("question_id")));
                    question.setQuestionText(rs.getString("question_text"));
                    question.setPoints(rs.getInt("points"));

                    Answer answer = new Answer(
                            question,
                            rs.getString("answer_text"),
                            rs.getBoolean("is_correct")
                    );
                    answer.setId(UUID.fromString(rs.getString("answer_id")));

                    answers.add(answer);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return answers;
    }


    public boolean isDeletedAnswerById(UUID id) {
        String deleteAnswerSQL = "DELETE FROM answer WHERE id = ?";
        try {
            PreparedStatement psDeleteAnswer = connection.prepareStatement(deleteAnswerSQL);
            psDeleteAnswer.setObject(1, id);
            int rowsAffected = psDeleteAnswer.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error while deleting answer with ID: " + id);
        }
    }
}

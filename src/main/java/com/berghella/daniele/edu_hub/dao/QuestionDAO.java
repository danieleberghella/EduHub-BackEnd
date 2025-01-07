package com.berghella.daniele.edu_hub.dao;

import com.berghella.daniele.edu_hub.model.Question;
import com.berghella.daniele.edu_hub.model.Test;
import com.berghella.daniele.edu_hub.utility.database.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class QuestionDAO {
    private final Connection connection = DatabaseConnection.getInstance().getConnection();

    public void createQuestion(Question question){
        String insertQuestionSQL = "INSERT INTO question(id, test_id, question_text, points) " + "VALUES (?, ?, ?, ?);";
        try {
            PreparedStatement psInsertQuestion = connection.prepareStatement(insertQuestionSQL);
            psInsertQuestion.setObject(1, question.getId());
            psInsertQuestion.setObject(2, question.getTest().getId());
            psInsertQuestion.setString(3, question.getQuestionText());
            psInsertQuestion.setDouble(4, question.getPoints());
            psInsertQuestion.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Question> getAllQuestions() {
        List<Question> questions = new ArrayList<>();
        String getAllQuestionsSQL = """
        SELECT
        q.id AS question_id,
                q.question_text,
                q.points,
                t.id AS test_id,
        t.name AS test_name
        FROM
        question q
        JOIN
        test t
        ON
        q.test_id = t.id
        """;
        try {
            Statement stm = connection.createStatement();
            ResultSet rs = stm.executeQuery(getAllQuestionsSQL);
            while (rs.next()){
                Test test = new Test();
                test.setId(UUID.fromString(rs.getString("test_id")));
                test.setTitle(rs.getString("test_name"));

                Question question = new Question();
                question.setId(UUID.fromString(rs.getString("question_id")));
                question.setTest(test);
                question.setQuestionText(rs.getString("question_text"));
                question.setPoints(rs.getDouble("points"));
                questions.add(question);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return questions;
    }

    public List<Question> getQuestionsByTestId(UUID testId) {
        List<Question> questions = new ArrayList<>();
        String getQuestionsByTestIdSQL = """
        SELECT
            q.id AS question_id,
            q.question_text,
            q.points,
            t.id AS test_id,
            t.title AS test_name
        FROM
            question q
        JOIN
            test t
        ON
            q.test_id = t.id
        WHERE
            t.id = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(getQuestionsByTestIdSQL)) {
            ps.setObject(1, testId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Test test = new Test();
                    test.setId(UUID.fromString(rs.getString("test_id")));
                    test.setTitle(rs.getString("test_name"));

                    Question question = new Question();
                    question.setId(UUID.fromString(rs.getString("question_id")));
                    question.setTest(test);
                    question.setQuestionText(rs.getString("question_text"));
                    question.setPoints(rs.getDouble("points"));

                    questions.add(question);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving questions for test_id: " + testId, e);
        }

        return questions;
    }

    public Optional<Question> getQuestionById(UUID id) {
        String selectQuestionByIdSQL = """
        SELECT 
            q.id AS question_id, 
            q.question_text, 
            q.points, 
            t.id AS test_id, 
            t.name AS test_name
        FROM 
            question q
        JOIN 
            test t
        ON 
            q.test_id = t.id
        WHERE 
            q.id = ?
    """;
        try {
            PreparedStatement psSelectQuestionById = connection.prepareStatement(selectQuestionByIdSQL);
            psSelectQuestionById.setObject(1, id);
            ResultSet rs = psSelectQuestionById.executeQuery();
            if (rs.next()) {
                Test test = new Test();
                test.setId(UUID.fromString(rs.getString("test_id")));
                test.setTitle(rs.getString("test_name"));

                Question question = new Question();
                question.setId(UUID.fromString(rs.getString("question_id")));
                question.setTest(test);
                question.setQuestionText(rs.getString("question_text"));
                question.setPoints(rs.getDouble("points"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public Question updateQuestionById(Question updatedQuestion, UUID oldQuestionId) {
        if (getQuestionById(oldQuestionId).isPresent()){
            StringBuilder sql = new StringBuilder("UPDATE question SET ");
            List<Object> parameters = new ArrayList<>();

            if (updatedQuestion.getTest() != null) {
                sql.append("test_id = ?, ");
                parameters.add(updatedQuestion.getTest().getId());
            }
            if (updatedQuestion.getQuestionText() != null) {
                sql.append("question_text = ?, ");
                parameters.add(updatedQuestion.getQuestionText());
            }
            if (updatedQuestion.getPoints() > 0) {
                sql.append("points = ?, ");
                parameters.add(updatedQuestion.getPoints());
            }

            sql.setLength(sql.length() - 2);
            sql.append(" WHERE id = ?");
            parameters.add(oldQuestionId);
            try {
                PreparedStatement psUpdateQuestion = connection.prepareStatement(sql.toString());
                for (int i = 0; i < parameters.size(); i++) {
                    psUpdateQuestion.setObject(i + 1, parameters.get(i));
                }
                psUpdateQuestion.executeUpdate();
                return getQuestionById(oldQuestionId).orElse(null);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public boolean isDeletedQuestionById(UUID id) {
        String deleteQuestionSQL = "DELETE FROM question WHERE id = ?";
        try {
            PreparedStatement psDeleteQuestion = connection.prepareStatement(deleteQuestionSQL);
            psDeleteQuestion.setObject(1, id);
            int rowsAffected = psDeleteQuestion.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error while deleting question with ID: " + id);
        }
    }
}

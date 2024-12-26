package com.berghella.daniele.edu_hub.dao;

import com.berghella.daniele.edu_hub.model.Course;
import com.berghella.daniele.edu_hub.model.Subject;
import com.berghella.daniele.edu_hub.model.Test;
import com.berghella.daniele.edu_hub.model.TestDTO;
import com.berghella.daniele.edu_hub.utility.database.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TestDAO {
    private final Connection connection = DatabaseConnection.getInstance().getConnection();

    public void createTest(Test test) {
        String insertTestSQL = "INSERT INTO test(id, course_id, subject_id, title, description, available_minutes) " + "VALUES (?, ?, ?, ?, ?, ?);";
        try {
            PreparedStatement psInsertTest = connection.prepareStatement(insertTestSQL);
            psInsertTest.setObject(1, test.getId());
            psInsertTest.setObject(2, test.getCourse().getId());
            psInsertTest.setObject(3, test.getSubject().getId());
            psInsertTest.setString(4, test.getTitle());
            psInsertTest.setString(5, test.getDescription());
            psInsertTest.setInt(6, test.getAvailableMinutes());
            psInsertTest.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Test> getAllTests() {
        List<Test> tests = new ArrayList<>();
        String getAllTestsSQL = """
                SELECT 
                    t.id AS test_id, 
                    t.title, 
                    t.description, 
                    t.available_minutes, 
                    c.id AS course_id, 
                    c.name AS course_name, 
                    s.id AS subject_id, 
                    s.name AS subject_name
                FROM 
                    test t
                JOIN 
                    course c
                ON 
                    t.course_id = c.id
                JOIN 
                    subject s
                ON 
                    t.subject_id = s.id
                """;
        try {
            Statement stm = connection.createStatement();
            ResultSet rs = stm.executeQuery(getAllTestsSQL);
            while (rs.next()) {
                Course course = new Course();
                course.setId(UUID.fromString(rs.getString("course_id")));
                course.setName(rs.getString("course_name"));

                Subject subject = new Subject();
                subject.setId(UUID.fromString(rs.getString("subject_id")));
                subject.setName(rs.getString("subject_name"));

                Test test = new Test();
                test.setId(UUID.fromString(rs.getString("test_id")));
                test.setCourse(course);
                test.setSubject(subject);
                test.setTitle(rs.getString("title"));
                test.setDescription(rs.getString("description"));
                test.setAvailableMinutes(rs.getInt("available_minutes"));
                tests.add(test);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return tests;
    }

    public Optional<Test> getTestById(UUID id) {
        String selectTestByIdSQL = """
                    SELECT 
                        t.id AS test_id, 
                        t.title, 
                        t.description, 
                        t.available_minutes, 
                        c.id AS course_id, 
                        c.name AS course_name, 
                        s.id AS subject_id, 
                        s.name AS subject_name
                    FROM 
                        test t
                    JOIN 
                        course c
                    ON 
                        t.course_id = c.id
                    JOIN 
                        subject s
                    ON 
                        t.subject_id = s.id
                    WHERE 
                        t.id = ?
                """;
        try {
            PreparedStatement psSelectTestById = connection.prepareStatement(selectTestByIdSQL);
            psSelectTestById.setObject(1, id);
            ResultSet rs = psSelectTestById.executeQuery();
            if (rs.next()) {
                Course course = new Course();
                course.setId(UUID.fromString(rs.getString("course_id")));
                course.setName(rs.getString("course_name"));

                Subject subject = new Subject();
                subject.setId(UUID.fromString(rs.getString("subject_id")));
                subject.setName(rs.getString("subject_name"));

                Test test = new Test();
                test.setId(UUID.fromString(rs.getString("test_id")));
                test.setCourse(course);
                test.setSubject(subject);
                test.setTitle(rs.getString("title"));
                test.setDescription(rs.getString("description"));
                test.setAvailableMinutes(rs.getInt("available_minutes"));
                return Optional.of(test);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public Test updateTestById(Test updatedTest, UUID oldTestId) {
        if (getTestById(oldTestId).isPresent()) {
            StringBuilder sql = new StringBuilder("UPDATE test SET ");
            List<Object> parameters = new ArrayList<>();

            if (updatedTest.getCourse() != null) {
                sql.append("course_id = ?, ");
                parameters.add(updatedTest.getCourse().getId());
            }
            if (updatedTest.getSubject() != null) {
                sql.append("subject_id = ?, ");
                parameters.add(updatedTest.getSubject().getId());
            }
            if (updatedTest.getTitle() != null) {
                sql.append("title = ?, ");
                parameters.add(updatedTest.getTitle());
            }
            if (updatedTest.getDescription() != null) {
                sql.append("description = ?, ");
                parameters.add(updatedTest.getDescription());
            }
            if (updatedTest.getAvailableMinutes() > 0) {
                sql.append("available_minutes = ?, ");
                parameters.add(updatedTest.getAvailableMinutes());
            }

            sql.setLength(sql.length() - 2);
            sql.append(" WHERE id = ?");
            parameters.add(oldTestId);
            try {
                PreparedStatement psUpdateTest = connection.prepareStatement(sql.toString());
                for (int i = 0; i < parameters.size(); i++) {
                    psUpdateTest.setObject(i + 1, parameters.get(i));
                }
                psUpdateTest.executeUpdate();
                return getTestById(oldTestId).orElse(null);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public boolean isDeletedTestById(UUID id) {
        String deleteTestSQL = "DELETE FROM test WHERE id = ?";
        try {
            PreparedStatement psDeleteTest = connection.prepareStatement(deleteTestSQL);
            psDeleteTest.setObject(1, id);
            int rowsAffected = psDeleteTest.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error while deleting test with ID: " + id);
        }
    }
}

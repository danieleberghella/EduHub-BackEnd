package com.berghella.daniele.edu_hub.dao;

import com.berghella.daniele.edu_hub.model.Course;
import com.berghella.daniele.edu_hub.model.Subject;
import com.berghella.daniele.edu_hub.model.Test;
import com.berghella.daniele.edu_hub.model.TestResultsDTO;
import com.berghella.daniele.edu_hub.utility.database.DatabaseConnection;

import java.sql.*;
import java.util.*;

public class TestDAO {
    private final Connection connection = DatabaseConnection.getInstance().getConnection();

    public void createTest(Test test) {
        String insertTestSQL = "INSERT INTO test(id, course_id, subject_id, title, available_minutes) " + "VALUES (?, ?, ?, ?, ?);";
        try {
            PreparedStatement psInsertTest = connection.prepareStatement(insertTestSQL);
            psInsertTest.setObject(1, test.getId());
            psInsertTest.setObject(2, test.getCourse().getId());
            psInsertTest.setObject(3, test.getSubject().getId());
            psInsertTest.setString(4, test.getTitle());
            psInsertTest.setInt(5, test.getAvailableMinutes());
            psInsertTest.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveTestResults(TestResultsDTO testResultsDTO) {
        String insertTestResultsSQL = "INSERT INTO test_result(id, test_id, title, student_id, course_id, score, test_length_in_seconds) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?);";

        String insertQuestionScoresSQL = "INSERT INTO test_result_question(id, test_result_id, question_id, score) " +
                "VALUES (?, ?, ?, ?);";

        String insertUserAnswersSQL = "INSERT INTO test_result_answers(id, test_result_id, question_id, answer_id) " +
                "VALUES (?, ?, ?, ?);";

        try {
            connection.setAutoCommit(false);

            PreparedStatement psInsertTest = connection.prepareStatement(insertTestResultsSQL);
            psInsertTest.setObject(1, testResultsDTO.getId());
            psInsertTest.setObject(2, testResultsDTO.getTestId());
            psInsertTest.setString(3, testResultsDTO.getTitle());
            psInsertTest.setObject(4, testResultsDTO.getStudentId());
            psInsertTest.setObject(5, testResultsDTO.getCourseId());
            psInsertTest.setDouble(6, testResultsDTO.getScore());
            psInsertTest.setInt(7, testResultsDTO.getTestDuration());
            psInsertTest.executeUpdate();

            PreparedStatement psInsertQuestionScores = connection.prepareStatement(insertQuestionScoresSQL);
            for (Map.Entry<UUID, Double> entry : testResultsDTO.getQuestionScores().entrySet()) {
                psInsertQuestionScores.setObject(1, UUID.randomUUID());
                psInsertQuestionScores.setObject(2, testResultsDTO.getId());
                psInsertQuestionScores.setObject(3, entry.getKey());
                psInsertQuestionScores.setDouble(4, entry.getValue());
                psInsertQuestionScores.addBatch();
            }
            psInsertQuestionScores.executeBatch();

            PreparedStatement psInsertUserAnswers = connection.prepareStatement(insertUserAnswersSQL);
            for (Map.Entry<UUID, Set<UUID>> entry : testResultsDTO.getQuestions().entrySet()) {
                UUID questionId = entry.getKey();
                for (UUID answerId : entry.getValue()) {
                    psInsertUserAnswers.setObject(1, UUID.randomUUID());
                    psInsertUserAnswers.setObject(2, testResultsDTO.getId());
                    psInsertUserAnswers.setObject(3, questionId);
                    psInsertUserAnswers.setObject(4, answerId);
                    psInsertUserAnswers.addBatch();
                }
            }
            psInsertUserAnswers.executeBatch();

            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                throw new RuntimeException("Rollback failed", rollbackException);
            }
            throw new RuntimeException(e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException("Failed to reset auto-commit", e);
            }
        }
    }


    public TestResultsDTO getTestResultsById(UUID testResultId) {
        String selectTestResultsSQL = """
            SELECT id, test_id, student_id, score, test_length_in_seconds, title, course_id 
            FROM test_result
            WHERE id = ?
            """;

        String selectQuestionScoresSQL = """
            SELECT question_id, score 
            FROM test_result_question 
            WHERE test_result_id = ?
            """;

        String selectUserAnswersSQL = """
            SELECT question_id, answer_id
            FROM test_result_answers
            WHERE test_result_id = ?
            """;

        try {
            PreparedStatement psSelectTest = connection.prepareStatement(selectTestResultsSQL);
            psSelectTest.setObject(1, testResultId);
            ResultSet rsTest = psSelectTest.executeQuery();

            if (!rsTest.next()) {
                throw new RuntimeException("Test result not found for ID: " + testResultId);
            }

            TestResultsDTO testResultsDTO = new TestResultsDTO();
            testResultsDTO.setId(rsTest.getObject("id", UUID.class));
            testResultsDTO.setTestId(rsTest.getObject("test_id", UUID.class));
            testResultsDTO.setStudentId(rsTest.getObject("student_id", UUID.class));
            testResultsDTO.setScore(rsTest.getDouble("score"));
            testResultsDTO.setTestDuration(rsTest.getInt("test_length_in_seconds"));
            testResultsDTO.setTitle(rsTest.getString("title"));
            testResultsDTO.setCourseId(rsTest.getObject("course_id", UUID.class));

            PreparedStatement psSelectQuestions = connection.prepareStatement(selectQuestionScoresSQL);
            psSelectQuestions.setObject(1, testResultId);
            ResultSet rsQuestions = psSelectQuestions.executeQuery();

            Map<UUID, Double> questionScores = new HashMap<>();
            while (rsQuestions.next()) {
                UUID questionId = rsQuestions.getObject("question_id", UUID.class);
                double score = rsQuestions.getDouble("score");
                questionScores.put(questionId, score);
            }
            testResultsDTO.setQuestionScores(questionScores);

            PreparedStatement psSelectUserAnswers = connection.prepareStatement(selectUserAnswersSQL);
            psSelectUserAnswers.setObject(1, testResultId);
            ResultSet rsUserAnswers = psSelectUserAnswers.executeQuery();

            Map<UUID, Set<UUID>> userAnswers = new HashMap<>();
            while (rsUserAnswers.next()) {
                UUID questionId = rsUserAnswers.getObject("question_id", UUID.class);
                UUID answerId = rsUserAnswers.getObject("answer_id", UUID.class);
                userAnswers.computeIfAbsent(questionId, k -> new HashSet<>()).add(answerId);
            }
            testResultsDTO.setQuestions(userAnswers);

            return testResultsDTO;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch test results", e);
        }
    }



    public List<TestResultsDTO> getTestResultsByUserIdAndCourseId(UUID userId, UUID courseId) {
        String query = """
    SELECT tr.id AS result_id, tr.test_id, tr.title, tr.student_id, tr.score, tr.test_length_in_seconds, 
           trq.question_id, trq.score AS question_score, tra.question_id AS answer_question_id, 
           tra.answer_id, tr.course_id
    FROM test_result tr
    LEFT JOIN test_result_question trq ON trq.test_result_id = tr.id
    LEFT JOIN test_result_answers tra ON tra.test_result_id = tr.id
    WHERE tr.student_id = ? AND tr.course_id = ?
    """;

        List<TestResultsDTO> results = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setObject(1, userId);
            ps.setObject(2, courseId);
            ResultSet rs = ps.executeQuery();

            Map<UUID, TestResultsDTO> resultsMap = new HashMap<>();

            while (rs.next()) {
                try {
                    UUID resultId = UUID.fromString(rs.getString("result_id"));

                    TestResultsDTO testResult = resultsMap.computeIfAbsent(resultId, id -> {
                        TestResultsDTO dto = new TestResultsDTO();
                        dto.setId(resultId);

                        try {
                            boolean isSuccess = rs.getDouble("score") >= 18;

                            dto.setTestId(UUID.fromString(rs.getString("test_id")));
                            dto.setTitle(rs.getString("title"));
                            dto.setStudentId(UUID.fromString(rs.getString("student_id")));
                            dto.setScore(rs.getDouble("score"));
                            dto.setSuccess(isSuccess);
                            dto.setTestDuration(rs.getInt("test_length_in_seconds"));
                            dto.setCourseId(UUID.fromString(rs.getString("course_id")));
                            dto.setQuestionScores(new HashMap<>());
                            dto.setQuestions(new HashMap<>());
                            return dto;
                        } catch (SQLException e) {
                            throw new RuntimeException("Error initializing TestResultsDTO for result ID: " + id, e);
                        }
                    });

                    String questionIdStr = rs.getString("question_id");
                    if (questionIdStr != null && !questionIdStr.isEmpty()) {
                        UUID questionId = UUID.fromString(questionIdStr);
                        double questionScore = rs.getDouble("question_score");
                        testResult.getQuestionScores().put(questionId, questionScore);
                    }

                    String answerQuestionIdStr = rs.getString("answer_question_id");
                    String answerIdStr = rs.getString("answer_id");
                    if (answerQuestionIdStr != null && !answerQuestionIdStr.isEmpty()
                            && answerIdStr != null && !answerIdStr.isEmpty()) {
                        UUID answerQuestionId = UUID.fromString(answerQuestionIdStr);
                        UUID answerId = UUID.fromString(answerIdStr);

                        testResult.getQuestions()
                                .computeIfAbsent(answerQuestionId, k -> new HashSet<>())
                                .add(answerId);
                    }
                } catch (SQLException e) {
                    throw new RuntimeException("Error processing result set row", e);
                }
            }

            results.addAll(resultsMap.values());
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving test results", e);
        }

        return results;
    }



    public List<Test> getAllTests() {
        List<Test> tests = new ArrayList<>();
        String getAllTestsSQL = """
                SELECT 
                    t.id AS test_id, 
                    t.title,
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
                test.setAvailableMinutes(rs.getInt("available_minutes"));
                tests.add(test);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return tests;
    }

    public List<Test> getTestsByCourseId(UUID courseId) {
        List<Test> tests = new ArrayList<>();
        String getAllTestsByCourseIdSQL = """
                SELECT 
                    t.id AS test_id, 
                    t.title,
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
                    t.course_id = ?
                """;
        try {
            PreparedStatement psSelectTestsByCourseId = connection.prepareStatement(getAllTestsByCourseIdSQL);
            psSelectTestsByCourseId.setObject(1, courseId);
            ResultSet rs = psSelectTestsByCourseId.executeQuery();
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

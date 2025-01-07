package com.berghella.daniele.edu_hub.service;

import com.berghella.daniele.edu_hub.dao.TestDAO;
import com.berghella.daniele.edu_hub.model.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class TestService {
    private final TestDAO testDAO = new TestDAO();
    private static final SubjectService subjectService = new SubjectService();
    private static final CourseService courseService = new CourseService();
    private static final QuestionService questionService = new QuestionService();
    private static final AnswerService answerService = new AnswerService();

    private TestDTO convertTestToTestDTO(Test test) {
        List<Question> questions = questionService.getQuestionsByTestId(test.getId());
        List<QuestionDTO> allQuestionsDTO = new ArrayList<>();
        for (Question question : questions) {

            List<Answer> allAnswers = answerService.getAnswersByQuestionId(question.getId());
            List<AnswerDTO> allAnswersDTO = new ArrayList<>();

            for (Answer answer : allAnswers) {
                AnswerDTO answerDTO = new AnswerDTO(
                        answer.getId(),
                        answer.getText(),
                        answer.isCorrectAnswer()
                );
                allAnswersDTO.add(answerDTO);
            }

            QuestionDTO questionDTO = new QuestionDTO(
                    question.getId(),
                    question.getQuestionText(),
                    question.getPoints(),
                    allAnswersDTO
            );
            allQuestionsDTO.add(questionDTO);
        }

        return new TestDTO(
                test.getId(),
                test.getCourse().getId(),
                test.getSubject().getId(),
                test.getTitle(),
                test.getAvailableMinutes(),
                allQuestionsDTO
        );
    }

    public Optional<UUID> createTestWithQuestionsAndAnswers(TestDTO testDTO) {
        Optional<Course> courseOP = courseService.getCourseById(testDTO.getCourseId());
        Optional<Subject> subjectOP = subjectService.getSubjectById(testDTO.getSubjectId());

        if (courseOP.isEmpty() || subjectOP.isEmpty()) {
            return Optional.empty();
        }

        Course course = courseOP.get();
        Subject subject = subjectOP.get();

        Test newTest = new Test(
                course,
                subject,
                testDTO.getTitle(),
                testDTO.getAvailableMinutes()
        );
        testDAO.createTest(newTest);

        for (QuestionDTO questionDTO : testDTO.getQuestions()) {
            Question newQuestion = new Question(
                    newTest,
                    questionDTO.getQuestionText(),
                    questionDTO.getPoints()
            );
            questionService.createQuestion(newQuestion);

            for (AnswerDTO answerDTO : questionDTO.getAnswers()) {
                Answer newAnswer = new Answer(
                        newQuestion,
                        answerDTO.getText(),
                        answerDTO.isCorrectAnswer()
                );
                answerService.createAnswer(newAnswer);
            }
        }

        return Optional.of(newTest.getId());
    }

    public List<TestDTO> getAllTests() {
        List<Test> allTests = testDAO.getAllTests();
        List<TestDTO> allTestsDTO = new ArrayList<>();
        for (Test test : allTests) {
            TestDTO testDTO = convertTestToTestDTO(test);
            allTestsDTO.add(testDTO);
        }
        return allTestsDTO;
    }

    public List<TestDTO> getTestsByCourseId(UUID courseId) {
        List<Test> allTests = testDAO.getTestsByCourseId(courseId);
        List<TestDTO> allTestsDTO = new ArrayList<>();
        for (Test test : allTests) {
            TestDTO testDTO = convertTestToTestDTO(test);
            allTestsDTO.add(testDTO);
        }
        return allTestsDTO;
    }

    public TestResultsDTO getTestResultsById(UUID testResultId) {
        return testDAO.getTestResultsById(testResultId);
    }

    public List<TestResultsDTO> getTestResultsByUserIdAndCourseId(UUID userId, UUID courseId) {
        return testDAO.getTestResultsByUserIdAndCourseId(userId, courseId);
    }

    public Optional<TestDTO> getTestById(UUID id) {
        Optional<Test> testOP = testDAO.getTestById(id);
        if (testOP.isPresent()) {
            Test test = testOP.get();
            return Optional.of(convertTestToTestDTO(test));
        }
        return Optional.empty();
    }

    public TestDTO updateTestById(Test testUpdate, UUID oldTestId) {
        Test test = testDAO.updateTestById(testUpdate, oldTestId);
        if (test != null) {
            return convertTestToTestDTO(test);
        } else {
            return null;
        }
    }

    public boolean isDeletedTestById(UUID id) {
        return testDAO.isDeletedTestById(id);
    }

    public TestResultsDTO saveAndCalculateTestResult(UUID testId, UUID userId, Map<UUID, Set<UUID>> userAnswers, int secondsLeft) {
        TestDTO testDTO = getTestById(testId).orElseThrow(() ->
                new IllegalStateException("Test not found for ID: " + testId));

        int testDuration = (testDTO.getAvailableMinutes() * 60) - secondsLeft;

        List<Question> questions = questionService.getQuestionsByTestId(testId);
        Map<UUID, Double> questionScores = calculateQuestionScores(testId, userAnswers, questions);

        double maxScore = questions.stream()
                .mapToDouble(Question::getPoints)
                .sum();

        double totalScore = questionScores.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        double scoreInThirtieths = (totalScore / maxScore) * 30;

        scoreInThirtieths = BigDecimal.valueOf(scoreInThirtieths)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();

        if (scoreInThirtieths < 0) {
            scoreInThirtieths = 0;
        }

        boolean isSuccess = scoreInThirtieths >= 18;

        TestResultsDTO testResultsDTO = new TestResultsDTO();
        testResultsDTO.setTestId(testId);
        testResultsDTO.setTitle(testDTO.getTitle());
        testResultsDTO.setStudentId(userId);
        testResultsDTO.setCourseId(testDTO.getCourseId());
        testResultsDTO.setScore(scoreInThirtieths);
        testResultsDTO.setSuccess(isSuccess);
        testResultsDTO.setQuestions(userAnswers);
        testResultsDTO.setTestDuration(testDuration);
        testResultsDTO.setQuestionScores(questionScores);

        testDAO.saveTestResults(testResultsDTO);

        return testResultsDTO;
    }

    public Map<UUID, Double> calculateQuestionScores(UUID testId, Map<UUID, Set<UUID>> userAnswers, List<Question> questions) {
        if (questions.isEmpty()) {
            throw new IllegalStateException("No questions found for the test with ID: " + testId);
        }

        Map<UUID, Double> questionScores = new HashMap<>();

        for (Question question : questions) {
            double questionValue = question.getPoints();
            List<Answer> correctAnswers = answerService.getAnswersByQuestionId(question.getId()).stream()
                    .filter(Answer::isCorrectAnswer)
                    .toList();

            Set<UUID> userSelectedAnswers = userAnswers.getOrDefault(question.getId(), Set.of());

            double pointPerCorrectAnswer = correctAnswers.isEmpty() ? 0 : questionValue / correctAnswers.size();

            boolean hasWrongAnswers = userSelectedAnswers.stream()
                    .anyMatch(userAnswerId -> correctAnswers.stream()
                            .noneMatch(correctAnswer -> correctAnswer.getId().equals(userAnswerId)));

            double questionScore = 0;

            if (hasWrongAnswers) {
                questionScore = -0.25 * userSelectedAnswers.size();
            } else {
                for (Answer correctAnswer : correctAnswers) {
                    if (userSelectedAnswers.contains(correctAnswer.getId())) {
                        questionScore += pointPerCorrectAnswer;
                    }
                }
            }

            questionScores.put(question.getId(), questionScore);
        }

        return questionScores;
    }

}

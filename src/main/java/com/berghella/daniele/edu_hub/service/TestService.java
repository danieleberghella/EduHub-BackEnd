package com.berghella.daniele.edu_hub.service;

import com.berghella.daniele.edu_hub.dao.TestDAO;
import com.berghella.daniele.edu_hub.model.*;

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
                test.getDescription(),
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
                testDTO.getDescription(),
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
        if (test != null){
            return convertTestToTestDTO(test);
        } else {
            return null;
        }
    }

    public boolean isDeletedTestById(UUID id) {
        return testDAO.isDeletedTestById(id);
    }

    public double calculateTestResult(UUID testId, Map<UUID, List<UUID>> userAnswers) {
        List<Question> questions = questionService.getQuestionsByTestId(testId);

        if (questions.isEmpty()) {
            throw new IllegalStateException("No questions found for the test with ID: " + testId);
        }

        double totalPoints = 0;
        double userPoints = 0;

        for (Question question : questions) {
            double questionValue = question.getPoints();
            List<Answer> correctAnswers = answerService.getAnswersByQuestionId(question.getId()).stream()
                    .filter(Answer::isCorrectAnswer)
                    .toList();

            List<UUID> userSelectedAnswers = userAnswers.getOrDefault(question.getId(), List.of());
            if (!correctAnswers.isEmpty()) {
                double pointPerCorrectAnswer = questionValue / correctAnswers.size();

                for (Answer answer : correctAnswers) {
                    if (userSelectedAnswers.contains(answer.getId())) {
                        userPoints += pointPerCorrectAnswer;
                    }
                }
            }

            totalPoints += questionValue;
        }

        if (totalPoints > 0) {
            return userPoints / totalPoints * 30;
        }
        return -1;
    }
}

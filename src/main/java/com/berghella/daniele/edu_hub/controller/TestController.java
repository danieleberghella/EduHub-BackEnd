package com.berghella.daniele.edu_hub.controller;

import com.berghella.daniele.edu_hub.auth.middleware.JwtAuthMiddleware;
import com.berghella.daniele.edu_hub.model.Test;
import com.berghella.daniele.edu_hub.model.TestDTO;
import com.berghella.daniele.edu_hub.model.TestResultsDTO;
import com.berghella.daniele.edu_hub.service.TestService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import java.util.*;

public class TestController {
    private static final TestService testService = new TestService();

    public void registerRoutes(Javalin app) {
        // http://localhost:8000/tests
//        app.before("/tests", ctx -> {
//            new JwtAuthMiddleware().handle(ctx);
//            String email = ctx.attribute("email");
//            if (email == null) {
//                throw new io.javalin.http.UnauthorizedResponse("Unauthorized");
//            }
//        });
//        app.before("/tests/*", ctx -> {
//            new JwtAuthMiddleware().handle(ctx);
//            String email = ctx.attribute("email");
//            if (email == null) {
//                throw new io.javalin.http.UnauthorizedResponse("Unauthorized");
//            }
//        });

        app.post("/tests", this::createTest);
        app.get("/tests", this::getAllTests);
        app.get("/tests/course/{courseId}", this::getTestsByCourseId);
        app.get("/tests/{id}", this::getTestById);
        app.put("/tests/{id}", this::updateTestById);
        app.delete("/tests/{id}", this::deleteTestById);
        app.post("/tests/results/{id}", this::receiveTestResults);
        app.get("/tests/results/{resultId}", this::getTestResultsById);
        app.get("/tests/results/user/{userId}/course/{courseId}", this::getTestResultsByUserIdAndCourseId);

    }

    private void createTest(Context ctx) {
        try {
            TestDTO newTestDTO = ctx.bodyAsClass(TestDTO.class);
            Optional<UUID> testId = testService.createTestWithQuestionsAndAnswers(newTestDTO);

            if (testId.isPresent()) {
                ctx.status(HttpStatus.CREATED).json(testId.get());
            } else {
                ctx.status(HttpStatus.BAD_REQUEST).result("Failed to create test with questions and answers");
            }

        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Invalid request");
        }
    }

    private void getAllTests(Context ctx) {
        try {
            List<TestDTO> tests = testService.getAllTests();

            if (!tests.isEmpty()) {
                ctx.status(HttpStatus.OK).json(tests);
            } else {
                ctx.status(HttpStatus.BAD_REQUEST).json("Tests not found");
            }

        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Invalid request");
        }
    }

    private void getTestsByCourseId(Context ctx) {
        List<TestDTO> testsByCourseId;
        try {
            UUID courseId = UUID.fromString(ctx.pathParam("courseId"));
            testsByCourseId = testService.getTestsByCourseId(courseId);
            ctx.status(HttpStatus.OK).json(testsByCourseId);
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).json("Invalid request");
        }
    }

    private void getTestById(Context ctx) {
        try {
            UUID id = UUID.fromString(ctx.pathParam("id"));
            Optional<TestDTO> testOP = testService.getTestById(id);

            if (testOP.isPresent()) {
                TestDTO testDTO = testOP.get();
                ctx.status(HttpStatus.OK).json(testDTO);
            } else {
                ctx.status(HttpStatus.BAD_REQUEST).json("Test not found");
            }

        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).json("Invalid request");
        }
    }

    private void updateTestById(Context ctx) {
        try {
            UUID id = UUID.fromString(ctx.pathParam("id"));
            Test testFormUpdate = ctx.bodyAsClass(Test.class);
            TestDTO testUpdatedDTO = testService.updateTestById(testFormUpdate, id);

            if (testUpdatedDTO != null) {
                ctx.status(HttpStatus.OK).json(testUpdatedDTO);
            } else {
                ctx.status(HttpStatus.BAD_REQUEST).json("Test not found");
            }

        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).json("Invalid request");
        }
    }

    private void deleteTestById(Context ctx) {
        try {
            UUID id = UUID.fromString(ctx.pathParam("id"));
            boolean isTestDeleted = testService.isDeletedTestById(id);

            if (isTestDeleted) {
                ctx.status(HttpStatus.OK).json("Test Deleted");
            } else {
                ctx.status(HttpStatus.BAD_REQUEST).json("No test found with ID: " + id);
            }

        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).json("Invalid request");
        }
    }

    private void receiveTestResults(Context ctx) {
        try {
            UUID testId = UUID.fromString(ctx.pathParam("id"));
            UUID userId = UUID.fromString(Objects.requireNonNull(ctx.queryParam("userId")));
            int secondsLeft = Integer.parseInt(Objects.requireNonNull(ctx.queryParam("secondsLeft")));

            ObjectMapper objectMapper = new ObjectMapper();
            Map<UUID, Set<UUID>> userAnswers = objectMapper.readValue(
                    ctx.body(),
                    new TypeReference<>() {
                    }
            );

            TestResultsDTO testResultsDTO = testService.saveAndCalculateTestResult(testId, userId, userAnswers, secondsLeft);

            ctx.status(HttpStatus.OK).json(testResultsDTO);
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Invalid request");
        }
    }

    private void getTestResultsById(Context ctx) {
        try {
            UUID resultId = UUID.fromString(ctx.pathParam("resultId"));
            TestResultsDTO testResultsDTO = testService.getTestResultsById(resultId);
            ctx.status(HttpStatus.OK).json(testResultsDTO);
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Invalid request");
        }
    }

    private void getTestResultsByUserIdAndCourseId(Context ctx) {
        try {
            UUID userId = UUID.fromString(ctx.pathParam("userId"));
            UUID courseId = UUID.fromString(ctx.pathParam("courseId"));

            List<TestResultsDTO> testResults = testService.getTestResultsByUserIdAndCourseId(userId, courseId);
            ctx.status(HttpStatus.OK).json(testResults);
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Invalid request");
        }
    }

}

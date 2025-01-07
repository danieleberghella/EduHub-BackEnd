package com.berghella.daniele.edu_hub.controller;

import com.berghella.daniele.edu_hub.auth.middleware.JwtAuthMiddleware;
import com.berghella.daniele.edu_hub.model.Course;
import com.berghella.daniele.edu_hub.model.EnrollmentDTO;
import com.berghella.daniele.edu_hub.model.User;
import com.berghella.daniele.edu_hub.service.EnrollmentService;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class EnrollmentController {
    private static final EnrollmentService enrollmentService = new EnrollmentService();

    public void registerRoutes(Javalin app) {
        // http://localhost:8000/enrollments
//        app.before("/enrollments", ctx -> {
//            new JwtAuthMiddleware().handle(ctx);
//            String email = ctx.attribute("email");
//            if (email == null) {
//                throw new io.javalin.http.UnauthorizedResponse("Unauthorized");
//            }
//        });
//        app.before("/enrollments/*", ctx -> {
//            new JwtAuthMiddleware().handle(ctx);
//            String email = ctx.attribute("email");
//            if (email == null) {
//                throw new io.javalin.http.UnauthorizedResponse("Unauthorized");
//            }
//        });
        app.post("/enrollments", this::enrollUsers);
        app.delete("/enrollments", this::deleteEnrollmentByUserIdAndCourseId);
    }

    private void enrollUsers(Context ctx) {
        try {
            List<UUID> userIds = ctx.bodyAsClass(EnrollmentDTO.class).getUserIds();
            UUID courseId = ctx.bodyAsClass(EnrollmentDTO.class).getCourseId();

            if (userIds == null || courseId == null || userIds.isEmpty()) {
                ctx.status(HttpStatus.BAD_REQUEST).json("Invalid input: userIds or courseId is missing");
                return;
            }

            List<UUID> enrolledUsers = new ArrayList<>();
            for (UUID userId : userIds) {
                try {
                    UUID newEnrollId = enrollmentService.enrollUser(userId, courseId, LocalDate.now());
                    if (newEnrollId != null) {
                        enrolledUsers.add(userId);
                    }
                } catch (Exception e) {
                    System.err.println("Failed to enroll userId: " + userId + ", error: " + e.getMessage());
                }
            }

            if (!enrolledUsers.isEmpty()) {
                ctx.status(HttpStatus.CREATED).json(enrolledUsers);
            } else {
                ctx.status(HttpStatus.OK).json("No users were enrolled");
            }
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Invalid request: " + e.getMessage());
        }
    }

    private void deleteEnrollmentByUserIdAndCourseId(Context ctx) {
        try {
            UUID userId = UUID.fromString(ctx.formParam("id"));
            UUID courseId = UUID.fromString(ctx.formParam("courseId"));
            boolean isEnrollmentDeleted = enrollmentService.isDeletedEnrollmentByUserIdAndCourseId(userId, courseId);
            if (isEnrollmentDeleted) {
                ctx.status(HttpStatus.OK).json("Enrollment Deleted");
            } else {
                ctx.status(HttpStatus.BAD_REQUEST).json("No enrollment found");
            }
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).json("Invalid request");
        }
    }


}

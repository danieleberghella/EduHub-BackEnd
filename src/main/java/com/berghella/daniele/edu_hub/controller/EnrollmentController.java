package com.berghella.daniele.edu_hub.controller;

import com.berghella.daniele.edu_hub.auth.middleware.JwtAuthMiddleware;
import com.berghella.daniele.edu_hub.model.Course;
import com.berghella.daniele.edu_hub.model.User;
import com.berghella.daniele.edu_hub.service.EnrollmentService;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class EnrollmentController {
    private static final EnrollmentService enrollmentService = new EnrollmentService();

    public void registerRoutes(Javalin app) {
        // http://localhost:8000/enrollments
        app.before("/enrollments", ctx -> {
            new JwtAuthMiddleware().handle(ctx);
            String email = ctx.attribute("email");
            if (email == null) {
                throw new io.javalin.http.UnauthorizedResponse("Unauthorized");
            }
        });
        app.before("/enrollments/*", ctx -> {
            new JwtAuthMiddleware().handle(ctx);
            String email = ctx.attribute("email");
            if (email == null) {
                throw new io.javalin.http.UnauthorizedResponse("Unauthorized");
            }
        });
        app.post("/enrollments", this::enrollUser);
        app.get("/enrollments/users/{courseId}", this::getUsersByCourse);
        app.get("/enrollments/courses/{userId}", this::getCoursesByUser);
        app.delete("/enrollments", this::deleteEnrollmentByUserIdAndCourseId);
    }

    private void enrollUser(Context ctx) {
        try{
            UUID userId = UUID.fromString(Objects.requireNonNull(ctx.formParam("userId")));
            UUID courseId = UUID.fromString(Objects.requireNonNull(ctx.formParam("courseId")));
            String enrollDate = ctx.formParam("enrollDate");
            UUID nerEnrollId = enrollmentService.enrollUser(userId, courseId, LocalDate.parse(enrollDate));
            if (nerEnrollId != null) {
                ctx.status(HttpStatus.CREATED).json(nerEnrollId);
            } else {
                ctx.status(HttpStatus.BAD_REQUEST).json("An error occurred. Enroll not created");
            }
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Invalid request");
        }
    }

    private void deleteEnrollmentByUserIdAndCourseId(Context ctx) {
        try {
            UUID userId = UUID.fromString(Objects.requireNonNull(ctx.formParam("userId")));
            UUID courseId = UUID.fromString(Objects.requireNonNull(ctx.formParam("courseId")));
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

    private void getCoursesByUser(Context ctx) {
        try {
            UUID userId = UUID.fromString(ctx.pathParam("userId"));
            List<Course> courses = enrollmentService.getCoursesByUser(userId);
            if (!courses.isEmpty()) {
                ctx.status(HttpStatus.OK).json(courses);
            } else {
                ctx.status(HttpStatus.BAD_REQUEST).result("No courses found for this user.");
            }
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Invalid request");
        }
    }

    private void getUsersByCourse(Context ctx) {
        try {
            UUID courseId = UUID.fromString(ctx.pathParam("courseId"));
            List<User> users = enrollmentService.getUsersByCourse(courseId);
            if (!users.isEmpty()) {
                ctx.status(HttpStatus.OK).json(users);
            } else {
                ctx.status(HttpStatus.BAD_REQUEST).result("No users found for this course.");
            }
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Invalid request");
        }
    }

}

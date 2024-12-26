package com.berghella.daniele.edu_hub.controller;

import com.berghella.daniele.edu_hub.auth.middleware.JwtAuthMiddleware;
import com.berghella.daniele.edu_hub.model.AttendanceDTO;
import com.berghella.daniele.edu_hub.service.AttendanceService;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import io.javalin.http.HttpStatus;

public class AttendanceController {

    private final AttendanceService attendanceService = new AttendanceService();

    public void registerRoutes(Javalin app) {
        // http://localhost:8000/attendance
//        app.before("/attendance", ctx -> {
//            new JwtAuthMiddleware().handle(ctx);
//            String email = ctx.attribute("email");
//            if (email == null) {
//                throw new io.javalin.http.UnauthorizedResponse("Unauthorized");
//            }
//        });
//        app.before("/attendance/*", ctx -> {
//            new JwtAuthMiddleware().handle(ctx);
//            String email = ctx.attribute("email");
//            if (email == null) {
//                throw new io.javalin.http.UnauthorizedResponse("Unauthorized");
//            }
//        });
        app.post("/attendance", this::markAttendance);
        app.get("/attendance/course/{courseId}", this::getAttendancesByCourse);
        app.get("/attendance/subject/{subjectId}", this::getAttendancesBySubject);
//        app.put("/attendance/{id}", this::updateAttendanceById);
//        app.delete("/attendance/{id}", this::deleteAttendanceById);
    }

    private void markAttendance(Context ctx) {
        try {
            UUID studentId = UUID.fromString(Objects.requireNonNull(ctx.formParam("studentId")));
            UUID courseId = UUID.fromString(Objects.requireNonNull(ctx.formParam("courseId")));
            UUID subjectId = UUID.fromString(Objects.requireNonNull(ctx.formParam("subjectId")));
            LocalDate attendanceDate = LocalDate.parse(Objects.requireNonNull(ctx.formParam("attendanceDate")));
            boolean isPresent = Boolean.parseBoolean(ctx.formParam("isPresent"));

            UUID attendanceId = attendanceService.markAttendance(studentId, courseId, attendanceDate, isPresent, subjectId);
            if (attendanceId != null) {
                ctx.status(HttpStatus.OK).json(attendanceId);
            } else {
                ctx.status(HttpStatus.BAD_REQUEST).result("An error occurred while saving the attendance");
            }
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Invalid request");
        }
    }

    private void getAttendancesByCourse(Context ctx) {
        try {
            UUID studentId = UUID.fromString(ctx.pathParam("studentId"));
            UUID courseId = UUID.fromString(ctx.pathParam("courseId"));

            List<AttendanceDTO> attendances = attendanceService.getUserAttendancesByCourseId(studentId, courseId);

            if (attendances.isEmpty()) {
                ctx.status(HttpStatus.NO_CONTENT).result("No attendances found for the given course.");
            } else {
                ctx.status(HttpStatus.OK).json(attendances);
            }
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Invalid request");
        }
    }

    private void getAttendancesBySubject(Context ctx) {
        try {
            UUID studentId = UUID.fromString(ctx.pathParam("studentId"));
            UUID subjectId = UUID.fromString(ctx.pathParam("subjectId"));

            List<AttendanceDTO> attendances = attendanceService.getUserAttendancesBySubjectId(studentId, subjectId);

            if (attendances.isEmpty()) {
                ctx.status(HttpStatus.NO_CONTENT).result("No attendances found for the given subject.");
            } else {
                ctx.status(HttpStatus.OK).json(attendances);
            }
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Invalid request");
        }
    }
}

package com.berghella.daniele.edu_hub.controller;

import com.berghella.daniele.edu_hub.auth.middleware.JwtAuthMiddleware;
import com.berghella.daniele.edu_hub.model.Subject;
import com.berghella.daniele.edu_hub.model.SubjectCourse;
import com.berghella.daniele.edu_hub.model.SubjectCourseDTO;
import com.berghella.daniele.edu_hub.service.SubjectCourseService;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SubjectCourseController {
    private final SubjectCourseService subjectCourseService = new SubjectCourseService();

    public void registerRoutes(Javalin app) {

//        app.before("/subject-course/*", ctx -> {
//            ctx.header("Access-Control-Allow-Origin", "http://localhost:5173");
//            ctx.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
//            ctx.header("Access-Control-Allow-Headers", "Authorization, Content-Type, Accept");
//            ctx.header("Access-Control-Expose-Headers", "Authorization");
//            new JwtAuthMiddleware().handle(ctx);
//            String userId = ctx.attribute("userId");
//            if (userId == null) {
//                throw new io.javalin.http.UnauthorizedResponse("Unauthorized");
//            }
//        });
        app.post("/subject-course", this::createSubjectCourse);
        app.get("/subject-course", this::getAllSubjectCourses);
        app.get("/subject-course/course/{courseId}", this::getSubjectsByCourseId);
        app.get("/subject-course/{id}", this::getSubjectCourseById);
        app.put("/subject-course/{id}", this::updateSubjectCourse);
        app.delete("/subject-course", this::deleteSubjectCourseBySubjectAndCourseId);
    }

    private void createSubjectCourse(Context ctx) {
        try {
            List<UUID> subjectIds = ctx.bodyAsClass(SubjectCourseDTO.class).getSubjectIds();
            UUID courseId = ctx.bodyAsClass(SubjectCourseDTO.class).getCourseId();

            if (subjectIds == null || courseId == null || subjectIds.isEmpty()) {
                ctx.status(HttpStatus.BAD_REQUEST).json("Invalid input: subjectIds or courseId is missing");
                return;
            }

            List<UUID> enrolledSubjects = new ArrayList<>();
            for (UUID subjectId : subjectIds) {
                try {
                    SubjectCourse subjectCourse = new SubjectCourse(UUID.randomUUID(), subjectId, courseId);
                    UUID newSubjectCourseId = subjectCourseService.createSubjectCourse(subjectCourse);
                    if (newSubjectCourseId != null) {
                        enrolledSubjects.add(subjectId);
                    }
                } catch (Exception e) {
                    System.err.println("Failed to enroll subjectId: " + subjectId + ", error: " + e.getMessage());
                }
            }

            if (!enrolledSubjects.isEmpty()) {
                ctx.status(HttpStatus.CREATED).json(enrolledSubjects);
            } else {
                ctx.status(HttpStatus.OK).json("No subjects were enrolled");
            }
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Invalid request: " + e.getMessage());
        }
    }

    private void getAllSubjectCourses(Context ctx) {
        List<SubjectCourse> subjectCourses = subjectCourseService.getAllSubjectCourses();
        ctx.status(HttpStatus.OK).json(subjectCourses);
    }

    private void getSubjectsByCourseId(Context ctx) {
        List<Subject> subjects;
        try {
            UUID courseId = UUID.fromString(ctx.pathParam("courseId"));
            boolean isEnrolled = Boolean.parseBoolean(ctx.queryParam("isEnrolled"));
            subjects = subjectCourseService.getSubjectsByCourseId(courseId, isEnrolled);
            ctx.status(HttpStatus.OK).json(subjects);
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).json("Invalid request");
        }
    }

    private void getSubjectCourseById(Context ctx) {
        UUID id = UUID.fromString(ctx.pathParam("id"));
        SubjectCourse subjectCourse = subjectCourseService.getSubjectCourseById(id);
        if (subjectCourse != null) {
            ctx.status(HttpStatus.OK).json(subjectCourse);
        } else {
            ctx.status(HttpStatus.NOT_FOUND).result("SubjectCourse not found");
        }
    }

    private void updateSubjectCourse(Context ctx) {
        UUID id = UUID.fromString(ctx.pathParam("id"));
        SubjectCourse subjectCourse = ctx.bodyAsClass(SubjectCourse.class);
        SubjectCourse updated = subjectCourseService.updateSubjectCourse(id, subjectCourse);
        if (updated != null) {
            ctx.status(HttpStatus.OK).json(updated);
        } else {
            ctx.status(HttpStatus.NOT_FOUND).result("SubjectCourse not found");
        }
    }

    private void deleteSubjectCourseBySubjectAndCourseId(Context ctx) {
        try {
            UUID subjectId = UUID.fromString(ctx.formParam("id"));
            UUID courseId = UUID.fromString(ctx.formParam("courseId"));
            boolean isDeleted = subjectCourseService.deleteSubjectCourseBySubjectAndCourseId(subjectId, courseId);
            if (isDeleted) {
                ctx.status(HttpStatus.OK).result("SubjectCourse deleted");
            } else {
                ctx.status(HttpStatus.BAD_REQUEST).result("No matching SubjectCourse found");
            }
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Invalid request");
        }
    }
}

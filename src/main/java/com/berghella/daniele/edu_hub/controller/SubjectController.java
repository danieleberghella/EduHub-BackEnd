package com.berghella.daniele.edu_hub.controller;

import com.berghella.daniele.edu_hub.auth.middleware.JwtAuthMiddleware;
import com.berghella.daniele.edu_hub.model.Subject;
import com.berghella.daniele.edu_hub.service.SubjectService;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SubjectController {
    private static final SubjectService subjectService = new SubjectService();

    public void registerRoutes(Javalin app) {
        // http://localhost:8000/subjects
//        app.before("/subjects", ctx -> {
//            new JwtAuthMiddleware().handle(ctx);
//            String email = ctx.attribute("email");
//            if (email == null) {
//                throw new io.javalin.http.UnauthorizedResponse("Unauthorized");
//            }
//        });
//        app.before("/subjects/*", ctx -> {
//            new JwtAuthMiddleware().handle(ctx);
//            String email = ctx.attribute("email");
//            if (email == null) {
//                throw new io.javalin.http.UnauthorizedResponse("Unauthorized");
//            }
//        });
        app.post("/subjects", this::createSubject);
        app.get("/subjects", this::getAllSubjects);
        app.get("/subjects/{id}", this::getSubjectById);
        app.put("/subjects/{id}", this::updateSubjectById);
        app.delete("/subjects/{id}", this::deleteSubjectById);
    }

    private void createSubject(Context ctx) {
        try{
            Subject newSubject = ctx.bodyAsClass(Subject.class);
            subjectService.createSubject(newSubject);
            ctx.status(HttpStatus.CREATED).json(newSubject.getId());
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Invalid request");
        }
    }

    private void getAllSubjects(Context ctx) {
        try {
            List<Subject> subjects = subjectService.getAllSubjects();
            ctx.status(HttpStatus.OK).json(subjects);
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Invalid request");
        }
    }

    private void getSubjectById(Context ctx) {
        Optional<Subject> subject;
        try {
            UUID id = UUID.fromString(ctx.pathParam("id"));
            subject = subjectService.getSubjectById(id);
            if (subject.isPresent()) {
                ctx.status(HttpStatus.OK).json(subject.get());
            } else {
                ctx.status(HttpStatus.BAD_REQUEST).json("Subject not found");
            }
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).json("Invalid request");
        }
    }

    private void updateSubjectById(Context ctx) {
        try {
            UUID id = UUID.fromString(ctx.pathParam("id"));
            Subject subjectFormUpdate = ctx.bodyAsClass(Subject.class);
            Subject subjectUpdated = subjectService.updateSubjectById(subjectFormUpdate, id);
            ctx.status(HttpStatus.OK).json(subjectUpdated);
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).json("Invalid request");
        }
    }

    private void deleteSubjectById(Context ctx) {
        try {
            UUID id = UUID.fromString(ctx.pathParam("id"));
            boolean isSubjectDeleted = subjectService.isDeletedSubjectById(id);
            if (isSubjectDeleted) {
                ctx.status(HttpStatus.OK).json("Subject Deleted");
            } else {
                ctx.status(HttpStatus.OK).json("No subject found with ID: " + id);
            }
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).json("Invalid request");
        }
    }
}

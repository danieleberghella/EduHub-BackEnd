package com.berghella.daniele.edu_hub.controller;


import com.berghella.daniele.edu_hub.auth.middleware.JwtAuthMiddleware;
import com.berghella.daniele.edu_hub.model.Course;
import com.berghella.daniele.edu_hub.service.CourseService;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CourseController {
    private static CourseService courseService = new CourseService();

    public void registerRoutes(Javalin app) {
        // http://localhost:8000/courses
//        app.before("/courses", ctx -> {
//            new JwtAuthMiddleware().handle(ctx);
//            String email = ctx.attribute("email");
//            if (email == null) {
//                throw new io.javalin.http.UnauthorizedResponse("Unauthorized");
//            }
//        });
//        app.before("/courses/*", ctx -> {
//            new JwtAuthMiddleware().handle(ctx);
//            String email = ctx.attribute("email");
//            if (email == null) {
//                throw new io.javalin.http.UnauthorizedResponse("Unauthorized");
//            }
//        });
        app.post("/courses", this::createCourse);
        app.get("/courses", this::getAllCourses);
        app.get("/courses/user/{userId}", this::getAllCoursesByUserId);
        app.get("/courses/{id}", this::getCourseById);
        app.put("/courses/{id}", this::updateCourseById);
        app.delete("/courses/{id}", this::deleteCourseById);
    }

    private void createCourse(Context ctx) {
        try {
            Course newCourse = ctx.bodyAsClass(Course.class);
            courseService.createCourse(newCourse);
            ctx.status(HttpStatus.CREATED).json(newCourse.getId());
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Invalid request");
        }
    }

    private void getAllCourses(Context ctx) {
        try {
            List<Course> courses = courseService.getAllCourses();
            if (!courses.isEmpty()) {
                ctx.status(HttpStatus.OK).json(courses);
            } else {
                ctx.status(HttpStatus.BAD_REQUEST).json("Course not found");
            }
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Invalid request");
        }
    }

    private void getAllCoursesByUserId(Context ctx) {
        try {
            UUID userId = UUID.fromString(ctx.pathParam("userId"));
            List<Course> courses = courseService.getAllCoursesByUserId(userId);
            ctx.status(HttpStatus.OK).json(courses);
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Invalid request");
        }
    }

    private void getCourseById(Context ctx) {
        Optional<Course> course;
        try {
            UUID id = UUID.fromString(ctx.pathParam("id"));
            course = courseService.getCourseById(id);
            if (course.isPresent()) {
                ctx.status(HttpStatus.OK).json(course.get());
            } else {
                ctx.status(HttpStatus.BAD_REQUEST).json("Course not found");
            }
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).json("Invalid request");
        }
    }

    private void updateCourseById(Context ctx) {
        try {
            UUID id = UUID.fromString(ctx.pathParam("id"));
            Course courseFormUpdate = ctx.bodyAsClass(Course.class);
            Course courseUpdated = courseService.updateCourseById(courseFormUpdate, id);
            if (courseUpdated != null) {
                ctx.status(HttpStatus.OK).json(courseUpdated);
            } else {
                ctx.status(HttpStatus.BAD_REQUEST).json("Course not found");
            }
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).json("Invalid request");
        }
    }

    private void deleteCourseById(Context ctx) {
        try {
            UUID id = UUID.fromString(ctx.pathParam("id"));
            boolean isCourseDeleted = courseService.isDeletedCourseById(id);
            if (isCourseDeleted) {
                ctx.status(HttpStatus.OK).json("Course Deleted");
            } else {
                ctx.status(HttpStatus.BAD_REQUEST).json("No course found with ID: " + id);
            }
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).json("Invalid request");
        }
    }

}

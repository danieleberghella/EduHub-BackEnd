package com.berghella.daniele.edu_hub;

import com.berghella.daniele.edu_hub.auth.controller.AuthController;
import com.berghella.daniele.edu_hub.controller.*;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;

public class App {
    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.jsonMapper(new JavalinJackson());
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(it -> {
                    it.allowHost("http://localhost:5173");
//                    it.anyHost();
//                    it.reflectClientOrigin = true;
//                    it.allowCredentials = true;
                });
            });
        }).start(8000);

        app.options("/*", ctx -> {
            ctx.header("Access-Control-Allow-Origin", "http://localhost:5173");
            ctx.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            ctx.header("Access-Control-Allow-Headers", "Authorization, Content-Type, Accept");
            ctx.header("Access-Control-Expose-Headers", "Authorization");
            ctx.status(204);
        });


        AuthController authController = new AuthController();
        authController.registerRoutes(app);

        MediaFileController mediaFileController = new MediaFileController();
        mediaFileController.registerRoutes(app);

        SubjectController subjectController = new SubjectController();
        subjectController.registerRoutes(app);

        UserController userController = new UserController();
        userController.registerRoutes(app);

        CourseController courseController = new CourseController();
        courseController.registerRoutes(app);

        TestController testController = new TestController();
        testController.registerRoutes(app);

        EnrollmentController enrollmentController = new EnrollmentController();
        enrollmentController.registerRoutes(app);

        AttendanceController attendanceController = new AttendanceController();
        attendanceController.registerRoutes(app);

        MessageController messageController = new MessageController();
        messageController.registerRoutes(app);

        SubjectCourseController subjectCourseController = new SubjectCourseController();
        subjectCourseController.registerRoutes(app);
    }
}
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
//                    it.allowHost("example.com", "javalin.io");
                    it.anyHost();
                });
            });
        }).start(8000);

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
    }
}
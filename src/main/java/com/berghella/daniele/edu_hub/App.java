package com.berghella.daniele.edu_hub;

import com.berghella.daniele.edu_hub.auth.controller.AuthController;
import io.javalin.Javalin;

public class App {
    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(it -> {
//                    it.allowHost("example.com", "javalin.io");
                    it.anyHost();
                });
            });
        }).start(8000);

        AuthController authController = new AuthController();
        authController.registerRoutes(app);
    }
}
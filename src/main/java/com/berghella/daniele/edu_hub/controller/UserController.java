package com.berghella.daniele.edu_hub.controller;

import com.berghella.daniele.edu_hub.auth.middleware.JwtAuthMiddleware;
import com.berghella.daniele.edu_hub.model.User;
import com.berghella.daniele.edu_hub.model.UserRole;
import com.berghella.daniele.edu_hub.service.UserService;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserController {
    private static UserService userService = new UserService();

    public void registerRoutes(Javalin app) {
        // http://localhost:8000/users

//        app.before("/users/*", ctx -> {
//            new JwtAuthMiddleware().handle(ctx);
//            String userId = ctx.attribute("userId");
//            if (userId == null) {
//                throw new io.javalin.http.UnauthorizedResponse("Unauthorized");
//            }
//        });
//        app.before("/users", ctx -> {
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
        app.get("/users", this::getAllUsers);
        app.get("/users/role/{role}", this::getAllUsersPerRole);
        app.get("/users/{id}", this::getUserById);
        app.get("/users/course/{courseId}", this::getUsersByCourseId);
        app.put("/users/{id}", this::updateUserById);
        app.delete("/users/{id}", this::deleteUserById);
    }

    private void getAllUsers(Context ctx) {
        try {
            List<User> users = userService.getAllUsers();
            ctx.status(HttpStatus.OK).json(users);
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Invalid request");
        }
    }

    private void getAllUsersPerRole(Context ctx) {
        try {
            UserRole role = UserRole.valueOf(ctx.pathParam("role").toUpperCase());
            List<User> users = userService.getAllUsersPerRole(role);
            ctx.status(HttpStatus.OK).json(users);
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Invalid request");
        }
    }

    private void getUserById(Context ctx) {
        Optional<User> user;
        try {
            UUID id = UUID.fromString(ctx.pathParam("id"));
            user = userService.getUserById(id);
            if (user.isPresent()) {
                ctx.status(HttpStatus.OK).json(user.get());
            } else {
                ctx.status(HttpStatus.BAD_REQUEST).json("User not found");
            }
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).json("Invalid request");
        }
    }

    private void getUsersByCourseId(Context ctx) {
        List<User> users;
        try {
            UUID courseId = UUID.fromString(ctx.pathParam("courseId"));
            boolean isEnrolled = Boolean.parseBoolean(ctx.queryParam("isEnrolled"));
            users = userService.getUsersByCourseId(courseId, isEnrolled);
            ctx.status(HttpStatus.OK).json(users);
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).json("Invalid request");
        }
    }

    private void updateUserById(Context ctx) {
        try {
            UUID id = UUID.fromString(ctx.pathParam("id"));
            User userFormUpdate = ctx.bodyAsClass(User.class);
            User userUpdated = userService.updateUserById(userFormUpdate, id);
            if (userUpdated != null) {
                ctx.status(HttpStatus.OK).json(userUpdated);
            } else {
                ctx.status(HttpStatus.BAD_REQUEST).json("User not found");
            }
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).json("Invalid request");
        }
    }

    private void deleteUserById(Context ctx) {
        try {
            UUID id = UUID.fromString(ctx.pathParam("id"));
            boolean isUserDeleted = userService.deleteUserById(id);
            if (isUserDeleted) {
                ctx.status(HttpStatus.OK).json("User Deleted");
            } else {
                ctx.status(HttpStatus.BAD_REQUEST).json("No user found with ID: " + id);
            }
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).json("Invalid request");
        }
    }
}

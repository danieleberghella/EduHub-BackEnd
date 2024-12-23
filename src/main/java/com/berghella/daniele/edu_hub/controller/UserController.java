package com.berghella.daniele.edu_hub.controller;

import com.berghella.daniele.edu_hub.model.User;
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
        app.post("/users", this::createUser);
        app.get("/users", this::getAllUsers);
        app.get("/users/{id}", this::getUserById);
        app.put("/users/{id}", this::updateUserById);
        app.delete("/users/{id}", this::deleteUserById);
    }

    private void createUser(Context ctx) {
        try{
            User newUser = ctx.bodyAsClass(User.class);
            userService.createUser(newUser);
            ctx.status(HttpStatus.CREATED).json(newUser.getId());
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Invalid request");
        }
    }

    private void getAllUsers(Context ctx) {
        try {
            List<User> users = userService.getAllUsers();
            if (!users.isEmpty()) {
                ctx.status(HttpStatus.OK).json(users);
            } else {
                ctx.status(HttpStatus.BAD_REQUEST).json("User not found");
            }
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

package com.berghella.daniele.edu_hub.controller;

import com.berghella.daniele.edu_hub.auth.middleware.JwtAuthMiddleware;
import com.berghella.daniele.edu_hub.model.Message;
import com.berghella.daniele.edu_hub.model.MessageDTO;
import com.berghella.daniele.edu_hub.service.MessageService;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

public class MessageController {
    private final MessageService messageService = new MessageService();

    public void registerRoutes(Javalin app) {
        // http://localhost:8000/messages
//        app.before("/messages", ctx -> {
//            new JwtAuthMiddleware().handle(ctx);
//            String userId = ctx.attribute("userId");
//            if (userId == null) {
//                throw new io.javalin.http.UnauthorizedResponse("Unauthorized");
//            }
//        });
//        app.before("/messages/*", ctx -> {
//            new JwtAuthMiddleware().handle(ctx);
//            String userId = ctx.attribute("userId");
//            if (userId == null) {
//                throw new io.javalin.http.UnauthorizedResponse("Unauthorized");
//            }
//        });
        app.post("/messages", this::createMessage);
        app.get("/messages/user/{userId}", this::getMessagesByUserId);
        app.get("/messages/{messageId}", this::getMessageById);
        app.delete("/messages/{messageId}", this::deleteMessageById);
        app.post("/messages/multiple", this::sendMessagesToMultipleReceivers);
    }

    private void createMessage(Context ctx) {
        try {
            MessageDTO messageDTO = ctx.bodyAsClass(MessageDTO.class);
            if (messageService.createMessage(messageDTO) != null) {
                ctx.status(HttpStatus.CREATED).result("Message created successfully");
            } else {
                ctx.status(HttpStatus.BAD_REQUEST).result("An error occurred while sending the message");
            }
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Invalid message data");
        }
    }

    private void getMessagesByUserId(Context ctx) {
        try {
            UUID userId = UUID.fromString(ctx.pathParam("userId"));
            ctx.status(HttpStatus.OK).json(messageService.getMessagesByUserId(userId));
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Invalid user ID");
        }
    }

    private void getMessageById(Context ctx) {
        try {
            UUID messageId = UUID.fromString(ctx.pathParam("messageId"));
            Optional<Message> message = messageService.getMessageById(messageId);

            if (message.isPresent()) {
                ctx.status(HttpStatus.OK).json(message.get());
            } else {
                ctx.status(HttpStatus.NOT_FOUND).result("Message not found");
            }
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Invalid message ID");
        }
    }

    private void deleteMessageById(Context ctx) {
        try {
            UUID messageId = UUID.fromString(ctx.pathParam("messageId"));
            boolean deleted = messageService.deleteMessageById(messageId);

            if (deleted) {
                ctx.status(HttpStatus.OK).json("Message Deleted");
            } else {
                ctx.status(HttpStatus.NOT_FOUND).result("Message not found");
            }
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Invalid message ID");
        }
    }

    private void sendMessagesToMultipleReceivers(Context ctx) {
        try {
            MessageDTO messageDTO = ctx.bodyAsClass(MessageDTO.class);
            List<UUID> messagesCreatedIds = messageService.sendMessagesToMultipleReceivers(messageDTO, messageDTO.getReceiverIds());
            if (!messagesCreatedIds.isEmpty()){
                ctx.status(HttpStatus.CREATED).result("Messages sent successfully");
            } else {
                ctx.status(HttpStatus.BAD_REQUEST).result("Impossible to send Messages");
            }
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Invalid data for sending messages");
        }
    }

}

package com.berghella.daniele.edu_hub.auth.middleware;

import com.berghella.daniele.edu_hub.auth.util.JwtUtil;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;

public class JwtAuthMiddleware implements Handler {

    @Override
    public void handle(Context ctx) throws Exception {
        String authHeader = ctx.header("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            ctx.status(HttpStatus.UNAUTHORIZED).result("Missing or invalid token");
            return;
        }

        String token = authHeader.replace("Bearer ", "");
        try {
            String email = JwtUtil.validateToken(token);
            ctx.attribute("email", email);
        } catch (Exception e) {
            ctx.status(HttpStatus.UNAUTHORIZED).result("Invalid or expired token");
        }
    }
}
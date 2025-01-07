package com.berghella.daniele.edu_hub.auth.middleware;

import com.berghella.daniele.edu_hub.auth.util.JwtUtil;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;

public class JwtAuthMiddleware implements Handler {

    @Override
    public void handle(Context ctx) throws Exception {
        System.out.println(ctx.headerMap());
        String authHeader = ctx.header("Authorization");
        String authHeader2 = ctx.header("authorization");
        String authHeader3 = ctx.header("AUTHORIZATION");


        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            ctx.status(HttpStatus.UNAUTHORIZED).result("Missing or invalid token");
            return;
        }

        String token = authHeader.replace("Bearer ", "");
        try {
            String userId = JwtUtil.validateToken(token);
            ctx.attribute("userId", userId);
        } catch (Exception e) {
            ctx.status(HttpStatus.UNAUTHORIZED).result("Invalid or expired token");
        }
    }
}
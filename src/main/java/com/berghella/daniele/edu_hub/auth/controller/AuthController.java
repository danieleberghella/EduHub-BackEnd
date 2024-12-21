package com.berghella.daniele.edu_hub.auth.controller;

import com.berghella.daniele.edu_hub.auth.model.Auth;
import com.berghella.daniele.edu_hub.auth.service.AuthService;
import com.berghella.daniele.edu_hub.auth.util.JwtUtil;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.Objects;
import java.util.UUID;

public class AuthController {
    public static final String GENSALT = BCrypt.gensalt();
    private final AuthService authService = new AuthService();

    public void registerRoutes(Javalin app) {
        app.post("/login", this::login);
        app.post("/signup", this::signup);
    }

    public void signup(Context ctx) {
        UUID userId = UUID.fromString(Objects.requireNonNull(ctx.formParam("userId")));
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");
        String hashedPassword = BCrypt.hashpw(password, GENSALT);
        try {
            Auth newAuth = new Auth(userId, email, hashedPassword);
            authService.addAuth(newAuth);
            ctx.status(HttpStatus.CREATED).json(newAuth.getUserId());
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Invalid request");
        }
    }

    public void login(Context ctx) {
        try {
            String email = ctx.formParam("email");
            String password = ctx.formParam("password");
            Auth auth = authService.getAuthByEmail(email);
            if (auth.getEmail().equals(email) && BCrypt.checkpw(password, auth.getPassword())){
                String token = JwtUtil.generateToken(email);
                ctx.json(new TokenResponse(token));
            } else {
                ctx.status(HttpStatus.UNAUTHORIZED).result("Invalid credentials");
            }
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Invalid request");
        }
    }

    private static class TokenResponse {
        public final String token;

        public TokenResponse(String token) {
            this.token = token;
        }
    }
}

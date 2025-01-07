package com.berghella.daniele.edu_hub.auth.controller;

import com.berghella.daniele.edu_hub.auth.model.Auth;
import com.berghella.daniele.edu_hub.auth.service.AuthService;
import com.berghella.daniele.edu_hub.auth.util.JwtUtil;
import com.berghella.daniele.edu_hub.model.MessageDTO;
import com.berghella.daniele.edu_hub.model.User;
import com.berghella.daniele.edu_hub.model.UserRole;
import com.berghella.daniele.edu_hub.service.MessageService;
import com.berghella.daniele.edu_hub.service.UserService;

import io.github.cdimascio.dotenv.Dotenv;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.time.LocalDate;
import java.util.UUID;

public class AuthController {
    public static final String GENSALT = BCrypt.gensalt();
    private static final Dotenv dotenv = Dotenv.load();
    private MessageService messageService = new MessageService();
    private final AuthService authService = new AuthService();
    private final UserService userService = new UserService();

    public void registerRoutes(Javalin app) {
        // http://localhost:8000/
        app.post("/login", this::login);
        app.post("/signup", this::signup);
    }

    public void signup(Context ctx) {
        try {
            String firstName = ctx.formParam("first_name");
            String lastName = ctx.formParam("last_name");
            String email = ctx.formParam("email");
            String birthdate = ctx.formParam("birthdate");

            User newUser = new User(firstName, lastName, email, UserRole.REGISTRATION, LocalDate.parse(birthdate));
            userService.createUser(newUser);

            String newUserCompleteName = newUser.getLastName() + " " + newUser.getFirstName();

            MessageDTO newMessageDTO = new MessageDTO(
                    newUser.getId(),
                    newUserCompleteName,
                    newUser.getRole(),
                    UUID.fromString(dotenv.get("ADMIN_ID")),
                    "ADMIN",
                    "New Sign Up Request",
                    "Wow! A new User requested to join our school!",
                    LocalDate.now()
            );
            messageService.createMessage(newMessageDTO);

            String password = ctx.formParam("password");
            String hashedPassword = BCrypt.hashpw(password, GENSALT);
            Auth newAuth = new Auth(newUser.getId(), newUser.getEmail(), hashedPassword);
            authService.addAuth(newAuth);

            ctx.status(HttpStatus.CREATED).json(newUser.getId());
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Invalid request");
        }
    }

    public void login(Context ctx) {
        try {
            String email = ctx.formParam("email");
            String password = ctx.formParam("password");
            Auth auth = authService.getAuthByEmail(email);
            if (auth.getEmail().equals(email) && BCrypt.checkpw(password, auth.getPassword())) {
                String token = JwtUtil.generateToken(auth.getUserId());
                ctx.status(HttpStatus.OK).json(new TokenResponse(token));
            } else {
                ctx.status(HttpStatus.FORBIDDEN).result("Invalid credentials");
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

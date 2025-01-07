package com.berghella.daniele.edu_hub.auth.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.Date;
import java.util.UUID;

public class JwtUtil {

    private static final Dotenv dotenv = Dotenv.load();

    private static final String SECRET = dotenv.get("SECRET");
    private static final Algorithm ALGORITHM = Algorithm.HMAC256(SECRET);
    private static final JWTVerifier VERIFIER = JWT.require(ALGORITHM).build();
    private static final long EXPIRATION_TIME = 3600000;

    public static String generateToken(UUID userId) {
        return JWT.create()
                .withSubject(userId.toString())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(ALGORITHM);
    }

    public static String validateToken(String token) throws JWTVerificationException {
        DecodedJWT jwt = VERIFIER.verify(token);
        return jwt.getSubject();
    }

}

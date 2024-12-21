package com.berghella.daniele.edu_hub.auth.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class Auth {
    private UUID id = UUID.randomUUID();
    private UUID userId;
    private String email;
    private String password;

    public Auth(UUID userId, String email, String password){
        this.userId = userId;
        this.email = email;
        this.password = password;
    }
}

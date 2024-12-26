package com.berghella.daniele.edu_hub.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
public class User {
    private UUID id = UUID.randomUUID();
    private String firstName;
    private String lastName;
    private String email;
    private UserRole role;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    public User(String firstName, String lastName, String email, UserRole role, LocalDate birthDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.birthDate = birthDate;
    }
}

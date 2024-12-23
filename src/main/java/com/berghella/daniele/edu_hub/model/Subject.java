package com.berghella.daniele.edu_hub.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class Subject {
    private UUID id = UUID.randomUUID();
    private String name;
    private String description;

    public Subject(String name, String description) {
        this.name = name;
        this.description = description;
    }
}

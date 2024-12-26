package com.berghella.daniele.edu_hub.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class Course {
    private UUID id = UUID.randomUUID();
    private String name;
    private String description;
    private int totalHours;

    public Course(String name, String description, int totalHours) {
        this.name = name;
        this.description = description;
        this.totalHours = totalHours;
    }
}

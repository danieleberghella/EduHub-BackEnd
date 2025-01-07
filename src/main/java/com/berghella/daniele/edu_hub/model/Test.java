package com.berghella.daniele.edu_hub.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class Test {
    private UUID id = UUID.randomUUID();
    private Course course;
    private Subject subject;
    private String title;
    private int availableMinutes;

    public Test(Course course, Subject subject, String title, int availableMinutes) {
        this.course = course;
        this.subject = subject;
        this.title = title;
        this.availableMinutes = availableMinutes;
    }
}

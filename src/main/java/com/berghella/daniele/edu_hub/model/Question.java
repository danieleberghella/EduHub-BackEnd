package com.berghella.daniele.edu_hub.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class Question {
    private UUID id = UUID.randomUUID();
    private Test test;
    private String questionText;
    private double points;

    public Question(Test test, String questionText, double points) {
        this.test = test;
        this.questionText = questionText;
        this.points = points;
    }
}

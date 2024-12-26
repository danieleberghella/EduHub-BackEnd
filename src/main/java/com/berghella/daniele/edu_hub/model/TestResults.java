package com.berghella.daniele.edu_hub.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class TestResults {
    private UUID id = UUID.randomUUID();
    private Test test;
    private User student;
    private double score;
    private int testLengthInMinutes;

    public TestResults(Test test, User student, double score, int testLengthInMinutes) {
        this.test = test;
        this.student = student;
        this.score = score;
        this.testLengthInMinutes = testLengthInMinutes;
    }
}

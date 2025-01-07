package com.berghella.daniele.edu_hub.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
public class TestResultsDTO {
    private UUID id = UUID.randomUUID();
    private UUID testId;
    private String title;
    private UUID studentId;
    private UUID courseId;
    private double score;
    private boolean success;
    private Map<UUID, Set<UUID>> questions;
    private int testDuration;
    private Map<UUID, Double> questionScores;
}

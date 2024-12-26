package com.berghella.daniele.edu_hub.model;

import java.util.List;
import java.util.UUID;

public class TestResultsDTO {
    private UUID studentId;
    private double score;
    private List<QuestionDTO> questions;
    private boolean isSuccess;
}

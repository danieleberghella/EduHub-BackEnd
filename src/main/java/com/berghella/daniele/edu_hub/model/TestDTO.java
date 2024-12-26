package com.berghella.daniele.edu_hub.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class TestDTO {
    private UUID id;
    private UUID courseId;
    private UUID subjectId;
    private String title;
    private String description;
    private int availableMinutes;
    private List<QuestionDTO> questions = new ArrayList<>();

    public TestDTO(UUID id, UUID courseId, UUID subjectId, String title, String description, int availableMinutes, List<QuestionDTO> questions) {
        this.id = id;
        this.courseId = courseId;
        this.subjectId = subjectId;
        this.title = title;
        this.description = description;
        this.availableMinutes = availableMinutes;
        this.questions = questions;
    }
}

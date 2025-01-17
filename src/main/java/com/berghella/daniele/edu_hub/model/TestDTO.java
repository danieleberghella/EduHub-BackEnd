package com.berghella.daniele.edu_hub.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestDTO {
    private UUID id;
    private UUID courseId;
    private UUID subjectId;
    private String title;
    private int availableMinutes;
    private List<QuestionDTO> questions = new ArrayList<>();

}

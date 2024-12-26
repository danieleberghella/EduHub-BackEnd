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
public class QuestionDTO {
    private UUID id;
    private String questionText;
    private double points;
    private List<AnswerDTO> answers = new ArrayList<>();

}

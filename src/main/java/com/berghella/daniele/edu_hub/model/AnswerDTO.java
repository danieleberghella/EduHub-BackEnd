package com.berghella.daniele.edu_hub.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerDTO {
    private UUID id;
    private String text;
    private boolean isCorrectAnswer;
}

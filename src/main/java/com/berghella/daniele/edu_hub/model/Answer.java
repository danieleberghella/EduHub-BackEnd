package com.berghella.daniele.edu_hub.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class Answer {
    private UUID id = UUID.randomUUID();
    private Question question;
    private String text;
    private boolean isCorrectAnswer;

    public Answer(Question question, String text, boolean isCorrectAnswer) {
        this.question = question;
        this.text = text;
        this.isCorrectAnswer = isCorrectAnswer;
    }
}

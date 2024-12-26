package com.berghella.daniele.edu_hub.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
public class Message {
    private UUID id = UUID.randomUUID();
    private User sender;
    private User receiver;
    private String messageSubject;
    private String text;
    private LocalDate sentAt;

    public Message(User sender, User receiver, String messageSubject, String text, LocalDate sentAt) {
        this.sender = sender;
        this.receiver = receiver;
        this.messageSubject = messageSubject;
        this.text = text;
        this.sentAt = sentAt;
    }
}

package com.berghella.daniele.edu_hub.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class MessageDTO {
    private UUID senderId;
    private String senderName;
    private UserRole senderRole;
    private UUID receiverId;
    private String receiverName;
    private String messageSubject;
    private String text;
    private LocalDate sentAt;
    private List<UUID> receiverIds;

    public MessageDTO(UUID senderId, String senderName, UserRole senderRole, UUID receiverId, String receiverName, String messageSubject, String text, LocalDate sentAt) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderRole = senderRole;
        this.receiverId = receiverId;
        this.receiverName = receiverName;
        this.messageSubject = messageSubject;
        this.text = text;
        this.sentAt = sentAt;
    }
}

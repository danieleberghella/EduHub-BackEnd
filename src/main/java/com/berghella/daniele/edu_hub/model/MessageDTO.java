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
    private UUID receiverId;
    private String messageSubject;
    private String text;
    private LocalDate sentAt;
    private List<UUID> receiverIds;

}

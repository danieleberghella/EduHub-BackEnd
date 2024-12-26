package com.berghella.daniele.edu_hub.service;

import com.berghella.daniele.edu_hub.dao.MessageDAO;
import com.berghella.daniele.edu_hub.model.Message;
import com.berghella.daniele.edu_hub.model.MessageDTO;
import com.berghella.daniele.edu_hub.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MessageService {
    private final MessageDAO messageDAO = new MessageDAO();
    private final UserService userService = new UserService();

    private Message messageDTOtoMessage(MessageDTO messageDTO, UUID receiverId) {
        return new Message(
                userService.getUserById(messageDTO.getSenderId()).get(),
                userService.getUserById(receiverId).get(),
                messageDTO.getMessageSubject(),
                messageDTO.getText(),
                messageDTO.getSentAt()
        );
    }

    public UUID createMessage(MessageDTO messageDTO) {
        Message message = messageDTOtoMessage(messageDTO, messageDTO.getReceiverId());
        return messageDAO.createMessage(message);
    }

    public List<Message> getMessagesByUserId(UUID userId) {
        return messageDAO.getMessagesByUserId(userId);
    }

    public Optional<Message> getMessageById(UUID messageId) {
        return messageDAO.getMessageById(messageId);
    }

    public boolean deleteMessageById(UUID messageId) {
        return messageDAO.isDeletedMessageById(messageId);
    }

    public List<UUID> sendMessagesToMultipleReceivers(MessageDTO baseMessage, List<UUID> receiverIds) {
        List<UUID> messagesCreatedIds = new ArrayList<>();
        for (UUID receiverId : receiverIds) {
            Message message = messageDTOtoMessage(baseMessage, receiverId);
            messagesCreatedIds.add(messageDAO.createMessage(message));
        }
        return messagesCreatedIds;
    }
}
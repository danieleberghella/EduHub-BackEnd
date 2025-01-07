package com.berghella.daniele.edu_hub.service;

import com.berghella.daniele.edu_hub.dao.MessageDAO;
import com.berghella.daniele.edu_hub.model.Message;
import com.berghella.daniele.edu_hub.model.MessageDTO;

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

    private MessageDTO messageToMessageDTO(Message message) {
        String senderCompleteName = message.getSender().getLastName() + " " + message.getSender().getFirstName();
        String receiverCompleteName = message.getReceiver().getLastName() + " " + message.getReceiver().getFirstName();
        return new MessageDTO(
                message.getSender().getId(),
                senderCompleteName,
                message.getSender().getRole(),
                message.getReceiver().getId(),
                receiverCompleteName,
                message.getMessageSubject(),
                message.getText(),
                message.getSentAt()
        );
    }


    public UUID createMessage(MessageDTO messageDTO) {
        Message message = messageDTOtoMessage(messageDTO, messageDTO.getReceiverId());
        return messageDAO.createMessage(message);
    }

    public List<MessageDTO> getMessagesByUserId(UUID userId) {
        List<Message> messagesByUserId = messageDAO.getMessagesByUserId(userId);
        List<MessageDTO> messagesDTOByUserId = new ArrayList<>();
        for (Message message : messagesByUserId) {
            MessageDTO messageDTO = messageToMessageDTO(message);
            messagesDTOByUserId.add(messageDTO);
        }
        return messagesDTOByUserId;
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
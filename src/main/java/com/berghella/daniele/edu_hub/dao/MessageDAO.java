package com.berghella.daniele.edu_hub.dao;

import com.berghella.daniele.edu_hub.model.*;
import com.berghella.daniele.edu_hub.utility.database.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MessageDAO {
    private static final Connection connection = DatabaseConnection.getInstance().getConnection();

    private User mapUserFromResultSet(ResultSet rs, String prefix) throws SQLException {
        User user = new User(
                rs.getString(prefix + "first_name"),
                rs.getString(prefix + "last_name"),
                rs.getString(prefix + "email"),
                UserRole.valueOf(rs.getString(prefix + "role")),
                rs.getTimestamp(prefix + "birthdate").toLocalDateTime().toLocalDate()
        );
        user.setId(UUID.fromString(rs.getString(prefix + "id")));
        return user;
    }


    private Message mapMessageFromResultSet(ResultSet rs) throws SQLException {
        User sender = mapUserFromResultSet(rs, "sender_");
        User receiver = mapUserFromResultSet(rs, "receiver_");

        Message message = new Message(
                sender,
                receiver,
                rs.getString("message_subject"),
                rs.getString("text"),
                rs.getTimestamp("sent_at").toLocalDateTime().toLocalDate()
        );
        message.setId(UUID.fromString(rs.getString("id")));

        return message;
    }

    public UUID createMessage(Message message){
        String insertMessageSQL = "INSERT INTO message(id, sender_id, receiver_id, message_subject, text, sent_at) " + "VALUES (?, ?, ?, ?, ?, ?);";
        try {
            PreparedStatement psInsertMessage = connection.prepareStatement(insertMessageSQL);
            psInsertMessage.setObject(1, message.getId());
            psInsertMessage.setObject(2, message.getSender().getId());
            psInsertMessage.setObject(3, message.getReceiver().getId());
            psInsertMessage.setString(4, message.getMessageSubject());
            psInsertMessage.setString(5, message.getText());
            psInsertMessage.setObject(6, message.getSentAt());
            psInsertMessage.executeUpdate();
            return message.getId();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Message> getMessagesByUserId(UUID userId) {
        List<Message> messages = new ArrayList<>();

        String getMessagesByUserIdSQL =
                "SELECT m.id, m.sender_id, m.receiver_id, m.message_subject, m.text, m.sent_at, " +
                        "u_sender.first_name AS sender_first_name, u_sender.last_name AS sender_last_name, " +
                        "u_sender.email AS sender_email, u_sender.role AS sender_role, u_sender.birthdate AS sender_birthdate, " +
                        "u_receiver.first_name AS receiver_first_name, u_receiver.last_name AS receiver_last_name, " +
                        "u_receiver.email AS receiver_email, u_receiver.role AS receiver_role, u_receiver.birthdate AS receiver_birthdate " +
                        "FROM public.message m " +
                        "JOIN public.users u_sender ON m.sender_id = u_sender.id " +
                        "JOIN public.users u_receiver ON m.receiver_id = u_receiver.id " +
                        "WHERE m.receiver_id = ?";

        try (PreparedStatement stm = connection.prepareStatement(getMessagesByUserIdSQL)) {
            stm.setObject(1, userId);

            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                messages.add(mapMessageFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return messages;
    }

    public Optional<Message> getMessageById(UUID id) {
        String selectMessageByIdSQL =
                "SELECT m.id, m.sender_id, m.receiver_id, m.message_subject, m.text, m.sent_at, " +
                        "u_sender.first_name AS sender_first_name, u_sender.last_name AS sender_last_name, " +
                        "u_sender.email AS sender_email, u_sender.role AS sender_role, u_sender.birthdate AS sender_birthdate, " +
                        "u_receiver.first_name AS receiver_first_name, u_receiver.last_name AS receiver_last_name, " +
                        "u_receiver.email AS receiver_email, u_receiver.role AS receiver_role, u_receiver.birthdate AS receiver_birthdate " +
                        "FROM public.message m " +
                        "JOIN public.users u_sender ON m.sender_id = u_sender.id " +
                        "JOIN public.users u_receiver ON m.receiver_id = u_receiver.id " +
                        "WHERE m.id = ?";

        try (PreparedStatement ps = connection.prepareStatement(selectMessageByIdSQL)) {
            ps.setObject(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapMessageFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving message by Id");
        }

        return Optional.empty();
    }

    public boolean isDeletedMessageById(UUID id) {
        String deleteMessageSQL = "DELETE FROM message WHERE id = ?";
        try {
            PreparedStatement psDeleteMessage = connection.prepareStatement(deleteMessageSQL);
            psDeleteMessage.setObject(1, id);
            int rowsAffected = psDeleteMessage.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error while deleting message with ID: " + id);
        }
    }


}

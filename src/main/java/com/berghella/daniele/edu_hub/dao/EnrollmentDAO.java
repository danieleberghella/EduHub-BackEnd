package com.berghella.daniele.edu_hub.dao;
import com.berghella.daniele.edu_hub.utility.database.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.UUID;

public class EnrollmentDAO {

    private final Connection connection = DatabaseConnection.getInstance().getConnection();

    public UUID enrollUser(UUID userId, UUID courseId, LocalDate enrollmentDate) {
        String sql = "INSERT INTO enrollment (id, user_id, course_id, enrollment_date) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            UUID enrollId = UUID.randomUUID();
            statement.setObject(1, enrollId);
            statement.setObject(2, userId);
            statement.setObject(3, courseId);
            statement.setDate(4, Date.valueOf(enrollmentDate));
            statement.executeUpdate();
            return enrollId;
        } catch (SQLException e) {
            throw new RuntimeException("Error while creating enrollment");
        }
    }

    public boolean isDeletedEnrollmentByUserIdAndCourseId(UUID userId, UUID courseId) {
        String sql = "DELETE FROM enrollment WHERE user_id = ? AND course_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, userId);
            statement.setObject(2, courseId);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error while deleting enrollment");
        }
    }

}

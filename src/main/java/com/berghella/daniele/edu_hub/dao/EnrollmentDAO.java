package com.berghella.daniele.edu_hub.dao;

import com.berghella.daniele.edu_hub.model.Course;
import com.berghella.daniele.edu_hub.model.User;
import com.berghella.daniele.edu_hub.model.UserRole;
import com.berghella.daniele.edu_hub.utility.database.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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

    public List<Course> getCoursesByUser(UUID userId) {
        String sql = "SELECT c.* FROM course c JOIN enrollment e ON c.id = e.course_id WHERE e.user_id = ?";
        List<Course> courses = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, userId);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Course course = new Course(
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getInt("total_hours")
                );
                course.setId(rs.getObject("id", UUID.class));
                courses.add(course);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    public List<User> getUsersByCourse(UUID courseId) {
        String sql = "SELECT u.* FROM users u JOIN enrollment e ON u.id = e.user_id WHERE e.course_id = ?";
        List<User> users = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, courseId);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                User user = new User(
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        UserRole.valueOf(rs.getString("role")),
                        rs.getTimestamp("birthdate").toLocalDateTime().toLocalDate()
                );
                user.setId(rs.getObject("id", UUID.class));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
}

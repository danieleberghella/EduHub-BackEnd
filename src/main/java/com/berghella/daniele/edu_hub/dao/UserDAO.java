package com.berghella.daniele.edu_hub.dao;

import com.berghella.daniele.edu_hub.model.User;
import com.berghella.daniele.edu_hub.model.UserRole;
import com.berghella.daniele.edu_hub.utility.database.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserDAO {
    private final Connection connection = DatabaseConnection.getInstance().getConnection();

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User(
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("email"),
                UserRole.valueOf(rs.getString("role")),
                rs.getDate("birthdate").toLocalDate()
        );
        user.setId(UUID.fromString(rs.getString("id")));
        return user;
    }

    public void createUser(User user) {
        String insertUserSQL = "INSERT INTO users(id, first_name, last_name, email, role) " + "VALUES (?, ?, ?, ?, ?);";
        try {
            PreparedStatement psInsertUser = connection.prepareStatement(insertUserSQL);
            psInsertUser.setObject(1, user.getId());
            psInsertUser.setString(2, user.getFirstName());
            psInsertUser.setString(3, user.getLastName());
            psInsertUser.setString(4, user.getEmail());
            psInsertUser.setString(5, user.getRole().toString());
            psInsertUser.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Statement stm = connection.createStatement();
             ResultSet rs = stm.executeQuery(sql)) {
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all users", e);
        }
        return users;
    }

    public List<User> getAllUsersPerRole(UserRole role) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, role.toString().toUpperCase());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving users by role", e);
        }
        return users;
    }

    public Optional<User> getUserById(UUID id) {
        String selectUserByIdSQL = "SELECT * FROM users WHERE id = ?";
        try {
            PreparedStatement psSelectUserById = connection.prepareStatement(selectUserByIdSQL);
            psSelectUserById.setObject(1, id);
            ResultSet rs = psSelectUserById.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public List<User> getUsersByCourseId(UUID courseId, boolean isEnrolled) {
        String selectUsersByCourseSQL;
        if (isEnrolled) {
            selectUsersByCourseSQL = "SELECT DISTINCT u.* " +
                    "FROM users u " +
                    "JOIN enrollment e ON u.id = e.user_id " +
                    "WHERE e.course_id = ?";
        } else {
            selectUsersByCourseSQL = "SELECT DISTINCT u.* " +
                    "FROM users u " +
                    "LEFT JOIN enrollment e ON u.id = e.user_id AND e.course_id = ? " +
                    "WHERE e.course_id IS NULL AND u.role <> 'ADMIN' AND u.role <> 'REGISTRATION'";
        }
        List<User> users = new ArrayList<>();
        try (PreparedStatement psSelectUsersByCourse = connection.prepareStatement(selectUsersByCourseSQL)) {
            psSelectUsersByCourse.setObject(1, courseId);
            ResultSet rs = psSelectUsersByCourse.executeQuery();
            if (!rs.next()) {
                return users;

            } else {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving users by course ID", e);
        }
        return users;
    }

    public User updateUserById(User updatedUser, UUID oldUserId) {
        if (getUserById(oldUserId).isPresent()) {
            StringBuilder sql = new StringBuilder("UPDATE users SET ");
            List<Object> parameters = new ArrayList<>();

            if (updatedUser.getFirstName() != null) {
                sql.append("first_name = ?, ");
                parameters.add(updatedUser.getFirstName());
            }
            if (updatedUser.getLastName() != null) {
                sql.append("last_name = ?, ");
                parameters.add(updatedUser.getLastName());
            }
            if (updatedUser.getEmail() != null) {
                sql.append("email = ?, ");
                parameters.add(updatedUser.getEmail());
            }
            if (updatedUser.getRole() != null) {
                sql.append("role = ?, ");
                parameters.add(updatedUser.getRole().toString());
            }

            if (updatedUser.getBirthDate() != null) {
                sql.append("birthdate = ?, ");
                parameters.add(updatedUser.getBirthDate());
            }

            sql.setLength(sql.length() - 2);
            sql.append(" WHERE id = ?");
            parameters.add(oldUserId);
            try {
                PreparedStatement psUpdateUser = connection.prepareStatement(sql.toString());
                for (int i = 0; i < parameters.size(); i++) {
                    psUpdateUser.setObject(i + 1, parameters.get(i));
                }
                psUpdateUser.executeUpdate();
                return getUserById(oldUserId).orElse(null);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public boolean deleteUserById(UUID id) {
        String deleteUserSQL = "DELETE FROM users WHERE id = ?";
        try {
            PreparedStatement psDeleteUser = connection.prepareStatement(deleteUserSQL);
            psDeleteUser.setObject(1, id);
            int rowsAffected = psDeleteUser.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error while deleting user with ID: " + id);
        }
    }
}

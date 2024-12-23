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

    public void createUser(User user){
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
        String getAllUsersSQL = "SELECT * FROM users";
        try {
            Statement stm = connection.createStatement();
            ResultSet rs = stm.executeQuery(getAllUsersSQL);
            while (rs.next()){
                User user = new User(
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        UserRole.valueOf(rs.getString("role")));
                user.setId(UUID.fromString(rs.getString("id")));
                users.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
                User user = new User(
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        UserRole.valueOf(rs.getString("role")));
                user.setId(id);
                return Optional.of(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public User updateUserById(User updatedUser, UUID oldUserId) {
        if (getUserById(oldUserId).isPresent()){
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
            throw new RuntimeException("Error while deleting user with ID: " + id, e);
        }
    }
}

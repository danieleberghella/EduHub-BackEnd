package com.berghella.daniele.edu_hub.auth.dao;

import com.berghella.daniele.edu_hub.auth.model.Auth;
import com.berghella.daniele.edu_hub.utility.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AuthDAO {
    private final Connection connection = DatabaseConnection.getInstance().getConnection();

    public void addAuth(Auth auth){
        String insertAuthSQL = "INSERT INTO auth(id, user_id, email, password) " + "VALUES (?, ?, ?, ?);";
        try {
            PreparedStatement psInsertAuth = connection.prepareStatement(insertAuthSQL);
            psInsertAuth.setObject(1, auth.getId());
            psInsertAuth.setObject(2, auth.getUserId());
            psInsertAuth.setString(3, auth.getEmail());
            psInsertAuth.setString(4, auth.getPassword());
            psInsertAuth.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Auth getAuthByEmail(String email){
        String selectAuthByEmailSQL = "SELECT (id, user_id, email, password) FROM auth WHERE email = ?";
        Auth auth = new Auth();
        try {
            PreparedStatement psSelectAuthByEmail = connection.prepareStatement(selectAuthByEmailSQL);
            psSelectAuthByEmail.setString(1, email);

            ResultSet rs = psSelectAuthByEmail.executeQuery();
            if (rs.next()) {
                auth.setId(UUID.fromString(rs.getString("id")));
                auth.setUserId(UUID.fromString(rs.getString("user_id")));
                auth.setEmail(rs.getString("email"));
                auth.setPassword(rs.getString("password"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return auth;
    }
}

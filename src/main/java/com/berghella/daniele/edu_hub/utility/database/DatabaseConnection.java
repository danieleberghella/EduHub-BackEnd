package com.berghella.daniele.edu_hub.utility.database;


import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private final Dotenv dotenv = Dotenv.load();
    private static DatabaseConnection instance;
    private Connection connection;

    private final String urlDB = dotenv.get("DB_URL");
    private final String userDB = dotenv.get("DB_USER");
    private final String pswDB = dotenv.get("DB_PASSWORD");

    private DatabaseConnection() {
        try {
            connection = DriverManager.getConnection(urlDB, userDB, pswDB);
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection(){
        return connection;
    }
}


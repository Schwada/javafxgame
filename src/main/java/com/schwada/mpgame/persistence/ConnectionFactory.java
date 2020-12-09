package com.schwada.mpgame.persistence;

import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    public static final String URL = "jdbc:sqlite:scores.db";

    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(URL);
            return conn;
        } catch (SQLException | ClassNotFoundException ex) {
            throw new RuntimeException("Error connecting to the database", ex);
        }
    }
}



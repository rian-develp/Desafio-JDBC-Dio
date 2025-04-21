package org.example.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConnectionConfig {

    public static Connection getConnection() throws SQLException {
        final String URL = "jdbc:mysql://localhost/desafio_dio";
        final String USER = "root";
        final String PASSWORD = "";

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

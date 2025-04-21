package org.example;

import org.example.persistence.migration.MigrationStrategy;

import java.sql.Connection;
import java.sql.SQLException;

import static org.example.config.ConnectionConfig.getConnection;

public class Main {
    public static void main(String[] args) throws SQLException {

        try(Connection connection = getConnection()){
            new MigrationStrategy(connection).executeMigration();
        }

        new MainMenu().execute();
    }
}
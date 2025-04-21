package org.example.persistence.migration;

import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.example.config.ConnectionConfig;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.SQLException;

public class MigrationStrategy {

    private final Connection connection;

    public MigrationStrategy(Connection connection) {
        this.connection = connection;
    }

    public void executeMigration() {

        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;

        try (FileOutputStream fileOutputStream = new FileOutputStream("liquibase.log")) {

            System.setOut(new PrintStream(fileOutputStream));
            System.setErr(new PrintStream(fileOutputStream));

            try (
                    Connection connection1 = ConnectionConfig.getConnection();
                    var jdbcConnection = new JdbcConnection(connection1);
            ) {

                var liquibase = new Liquibase("db/changelog/db.changelog-master.yml",
                        new ClassLoaderResourceAccessor(),
                        jdbcConnection
                );

                liquibase.update();

            } catch (SQLException | LiquibaseException e) {
                e.printStackTrace();

            }
            System.setErr(originalErr);


        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            System.setOut(originalOut);
            System.setErr(originalErr);
        }

    }
}

package com.example.ExpenseApproval.repository;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Component
public class DatabaseInitializer {

    private final DatabaseConnection databaseConnection;

    public DatabaseInitializer(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @PostConstruct
    public void addMissingColumns() {
        if (!columnExists("users", "password_hash")) {
            execute("ALTER TABLE users ADD COLUMN password_hash VARCHAR(64)");
        }
    }

    private boolean columnExists(String tableName, String columnName) {
        String sql = """
                SELECT COUNT(*) AS count
                FROM information_schema.columns
                WHERE table_schema = DATABASE()
                  AND table_name = ?
                  AND column_name = ?
                """;

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, tableName);
            statement.setString(2, columnName);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() && resultSet.getInt("count") > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check database columns", e);
        }
    }

    private void execute(String sql) {
        try (Connection connection = databaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update database schema", e);
        }
    }
}

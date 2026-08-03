package com.example.ExpenseApproval.repository;

import com.example.ExpenseApproval.model.ExpenseAudit;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

@Repository
public class AuditRepository {

    private final DatabaseConnection databaseConnection;

    public AuditRepository(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public ExpenseAudit save(ExpenseAudit audit) {
        String sql = """
                INSERT INTO expense_audit (expense_id, changed_by, from_status, to_status, `timestamp`)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setLong(1, audit.getExpense().getId());
            statement.setLong(2, audit.getChangedBy().getId());
            statement.setString(3, audit.getFromStatus());
            statement.setString(4, audit.getToStatus());
            statement.setTimestamp(5, Timestamp.valueOf(audit.getTimestamp()));
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    audit.setId(keys.getLong(1));
                }
            }
            return audit;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save audit record", e);
        }
    }
}

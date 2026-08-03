package com.example.ExpenseApproval.repository;

import com.example.ExpenseApproval.model.*;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@Repository
public class ExpenseRepository {

    private final DatabaseConnection databaseConnection;

    public ExpenseRepository(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public Expense save(Expense expense) {
        if (expense.getId() == null) {
            return insert(expense);
        }
        return update(expense);
    }

    public Optional<Expense> findById(Long id) {
        String sql = baseExpenseQuery() + " WHERE e.id = ?";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapExpense(resultSet));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find expense by id", e);
        }
    }

    public List<Expense> findAll() {
        String sql = baseExpenseQuery() + " ORDER BY e.id";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            return mapExpenseList(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find expenses", e);
        }
    }

    public List<Expense> findBySubmittedBy(User user) {
        String sql = baseExpenseQuery() + " WHERE e.submitted_by = ? ORDER BY e.id";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, user.getId());

            try (ResultSet resultSet = statement.executeQuery()) {
                return mapExpenseList(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find expenses by user", e);
        }
    }

    public List<Expense> findByStatus(ExpenseStatus status) {
        String sql = baseExpenseQuery() + " WHERE e.status = ? ORDER BY e.id";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, status.name());

            try (ResultSet resultSet = statement.executeQuery()) {
                return mapExpenseList(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find expenses by status", e);
        }
    }

    private Expense insert(Expense expense) {
        String sql = """
                INSERT INTO expenses (amount, description, status, submitted_by, approved_by)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setBigDecimal(1, expense.getAmount());
            statement.setString(2, expense.getDescription());
            statement.setString(3, expense.getStatus().name());
            statement.setLong(4, expense.getSubmittedBy().getId());

            if (expense.getApprovedBy() == null) {
                statement.setNull(5, java.sql.Types.BIGINT);
            } else {
                statement.setLong(5, expense.getApprovedBy().getId());
            }

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    expense.setId(keys.getLong(1));
                }
            }
            return expense;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert expense", e);
        }
    }

    private Expense update(Expense expense) {
        String sql = """
                UPDATE expenses
                SET amount = ?, description = ?, status = ?, submitted_by = ?, approved_by = ?
                WHERE id = ?
                """;

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setBigDecimal(1, expense.getAmount());
            statement.setString(2, expense.getDescription());
            statement.setString(3, expense.getStatus().name());
            statement.setLong(4, expense.getSubmittedBy().getId());

            if (expense.getApprovedBy() == null) {
                statement.setNull(5, java.sql.Types.BIGINT);
            } else {
                statement.setLong(5, expense.getApprovedBy().getId());
            }

            statement.setLong(6, expense.getId());
            statement.executeUpdate();
            return expense;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update expense", e);
        }
    }

    private String baseExpenseQuery() {
        return """
                SELECT
                    e.id AS expense_id,
                    e.amount,
                    e.description,
                    e.status,
                    submitted_user.id AS submitted_user_id,
                    submitted_user.name AS submitted_user_name,
                    submitted_user.role AS submitted_user_role,
                    approved_user.id AS approved_user_id,
                    approved_user.name AS approved_user_name,
                    approved_user.role AS approved_user_role
                FROM expenses e
                JOIN users submitted_user ON e.submitted_by = submitted_user.id
                LEFT JOIN users approved_user ON e.approved_by = approved_user.id
                """;
    }

    private List<Expense> mapExpenseList(ResultSet resultSet) throws SQLException {
        List<Expense> expenses = new ArrayList<>();
        while (resultSet.next()) {
            expenses.add(mapExpense(resultSet));
        }
        return expenses;
    }

    private Expense mapExpense(ResultSet resultSet) throws SQLException {
        Expense expense = new Expense();
        expense.setId(resultSet.getLong("expense_id"));
        expense.setAmount(resultSet.getBigDecimal("amount"));
        expense.setDescription(resultSet.getString("description"));
        expense.setStatus(ExpenseStatus.valueOf(resultSet.getString("status")));
        expense.setSubmittedBy(mapUser(resultSet, "submitted_user"));

        Long approvedUserId = resultSet.getLong("approved_user_id");
        if (!resultSet.wasNull()) {
            expense.setApprovedBy(mapUser(resultSet, "approved_user"));
        }

        return expense;
    }

    private User mapUser(ResultSet resultSet, String prefix) throws SQLException {
        User user = new User();
        user.setId(resultSet.getLong(prefix + "_id"));
        user.setName(resultSet.getString(prefix + "_name"));
        user.setRole(Role.valueOf(resultSet.getString(prefix + "_role")));
        return user;
    }
}

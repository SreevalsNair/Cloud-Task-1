package com.example.ExpenseApproval.repository;

import com.example.ExpenseApproval.model.Role;
import com.example.ExpenseApproval.model.User;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

@Repository
public class UserRepository {

    private final DatabaseConnection databaseConnection;

    public UserRepository(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public Optional<User> findById(Long id) {
        String sql = "SELECT id, name, role, password_hash FROM users WHERE id = ?";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapUser(resultSet));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find user by id", e);
        }
    }

    public Optional<User> findByName(String name) {
        String sql = "SELECT id, name, role, password_hash FROM users WHERE LOWER(name) = LOWER(?)";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, name);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapUser(resultSet));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find user by name", e);
        }
    }

    public void updatePasswordHash(Long userId, String passwordHash) {
        String sql = "UPDATE users SET password_hash = ? WHERE id = ?";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, passwordHash);
            statement.setLong(2, userId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update user password", e);
        }
    }

    public User create(String name, Role role, String passwordHash) {
        String sql = "INSERT INTO users (name, role, password_hash) VALUES (?, ?, ?)";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, name);
            statement.setString(2, role.name());
            statement.setString(3, passwordHash);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    User user = new User();
                    user.setId(keys.getLong(1));
                    user.setName(name);
                    user.setRole(role);
                    user.setPasswordHash(passwordHash);
                    return user;
                }
            }

            throw new RuntimeException("Failed to create user.");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create user", e);
        }
    }

    private User mapUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getLong("id"));
        user.setName(resultSet.getString("name"));
        user.setRole(Role.valueOf(resultSet.getString("role")));
        user.setPasswordHash(resultSet.getString("password_hash"));
        return user;
    }
}

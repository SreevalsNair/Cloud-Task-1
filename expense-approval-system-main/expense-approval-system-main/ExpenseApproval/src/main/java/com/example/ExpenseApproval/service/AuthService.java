package com.example.ExpenseApproval.service;

import com.example.ExpenseApproval.dto.LoginResponse;
import com.example.ExpenseApproval.exception.UnauthorizedActionException;
import com.example.ExpenseApproval.model.Role;
import com.example.ExpenseApproval.model.User;
import com.example.ExpenseApproval.repository.UserRepository;
import com.example.ExpenseApproval.util.PasswordUtil;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LoginResponse login(String name, String role, String password) {
        if (name == null || name.isBlank() || role == null || role.isBlank()
                || password == null || password.isBlank()) {
            throw new UnauthorizedActionException("Name, role, and password are required.");
        }

        Role selectedRole = parseRole(role);
        String cleanName = name.trim();
        String enteredHash = PasswordUtil.hash(password);

        User user = userRepository.findByName(cleanName).orElse(null);

        if (user == null) {
            User createdUser = userRepository.create(cleanName, selectedRole, enteredHash);
            return new LoginResponse(createdUser.getId(), createdUser.getName(), createdUser.getRole(), true);
        }

        if (user.getRole() != selectedRole) {
            throw new UnauthorizedActionException(
                    "This user is registered as " + user.getRole() + ", not " + selectedRole + ".");
        }

        boolean firstLogin = user.getPasswordHash() == null || user.getPasswordHash().isBlank();

        if (firstLogin) {
            userRepository.updatePasswordHash(user.getId(), enteredHash);
            return new LoginResponse(user.getId(), user.getName(), user.getRole(), true);
        }

        if (!user.getPasswordHash().equals(enteredHash)) {
            throw new UnauthorizedActionException("Invalid password.");
        }

        return new LoginResponse(user.getId(), user.getName(), user.getRole(), false);
    }

    private Role parseRole(String role) {
        try {
            return Role.valueOf(role.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new UnauthorizedActionException("Invalid role selected.");
        }
    }
}

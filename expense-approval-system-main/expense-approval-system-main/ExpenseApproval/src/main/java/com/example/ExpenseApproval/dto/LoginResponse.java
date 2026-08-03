package com.example.ExpenseApproval.dto;

import com.example.ExpenseApproval.model.Role;

public class LoginResponse {

    private Long id;
    private String name;
    private Role role;
    private boolean firstLogin;

    public LoginResponse(Long id, String name, Role role, boolean firstLogin) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.firstLogin = firstLogin;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Role getRole() {
        return role;
    }

    public boolean isFirstLogin() {
        return firstLogin;
    }
}

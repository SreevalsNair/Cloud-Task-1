package com.example.ExpenseApproval.model;

public class User {

    private Long id;
    private String name;
    private Role role;
    private String passwordHash;

    public Long getId() { return id; }
    public String getName() { return name; }
    public Role getRole() { return role; }
    public String getPasswordHash() { return passwordHash; }
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setRole(Role role) { this.role = role; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
}

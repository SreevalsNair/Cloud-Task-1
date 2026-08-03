package com.example.ExpenseApproval.model;

import java.time.LocalDateTime;

public class ExpenseAudit {

    private Long id;
    private Expense expense;
    private User changedBy;
    private String fromStatus;
    private String toStatus;
    private LocalDateTime timestamp;

    public Long getId() { return id; }
    public Expense getExpense() { return expense; }
    public User getChangedBy() { return changedBy; }
    public String getFromStatus() { return fromStatus; }
    public String getToStatus() { return toStatus; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setId(Long id) { this.id = id; }
    public void setExpense(Expense expense) { this.expense = expense; }
    public void setChangedBy(User user) { this.changedBy = user; }
    public void setFromStatus(String s) { this.fromStatus = s; }
    public void setToStatus(String s) { this.toStatus = s; }
    public void setTimestamp(LocalDateTime t) { this.timestamp = t; }
}

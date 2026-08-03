package com.example.ExpenseApproval.model;

import java.math.BigDecimal;

public class Expense {

    private Long id;
    private BigDecimal amount;
    private String description;
    private ExpenseStatus status;
    private User submittedBy;
    private User approvedBy;

    public Long getId() { return id; }
    public BigDecimal getAmount() { return amount; }
    public String getDescription() { return description; }
    public ExpenseStatus getStatus() { return status; }
    public User getSubmittedBy() { return submittedBy; }
    public User getApprovedBy() { return approvedBy; }
    public void setId(Long id) { this.id = id; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setDescription(String description) { this.description = description; }
    public void setStatus(ExpenseStatus status) { this.status = status; }
    public void setSubmittedBy(User user) { this.submittedBy = user; }
    public void setApprovedBy(User user) { this.approvedBy = user; }
}

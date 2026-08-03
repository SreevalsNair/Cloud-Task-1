package com.example.ExpenseApproval.dto;

import java.math.BigDecimal;

public class ExpenseRequest {

    private Long userId;
    private BigDecimal amount;
    private String description;

    public ExpenseRequest() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
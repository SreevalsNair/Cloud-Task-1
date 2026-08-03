package com.example.ExpenseApproval.dto;

public class ActionRequest {

    private Long userId;

    public ActionRequest() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
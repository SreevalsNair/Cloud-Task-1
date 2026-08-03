package com.example.ExpenseApproval.controller;

import com.example.ExpenseApproval.dto.ActionRequest;
import com.example.ExpenseApproval.dto.ExpenseRequest;
import com.example.ExpenseApproval.model.Expense;
import com.example.ExpenseApproval.service.ExpenseService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = {"http://3.111.158.52:5173"})
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping("/submit")
    public ResponseEntity<Expense> submit(@RequestBody ExpenseRequest request) {
        Expense expense = expenseService.submitExpense(
                request.getUserId(),
                request.getAmount(),
                request.getDescription());
        return ResponseEntity.ok(expense);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<Expense> approve(
            @PathVariable Long id,
            @RequestBody ActionRequest request) {

        Expense expense = expenseService.approveExpense(id, request.getUserId());
        return ResponseEntity.ok(expense);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<Expense> reject(
            @PathVariable Long id,
            @RequestBody ActionRequest request) {

        Expense expense = expenseService.rejectExpense(id, request.getUserId());
        return ResponseEntity.ok(expense);
    }

    @PutMapping("/{id}/reimburse")
    public ResponseEntity<Expense> reimburse(
            @PathVariable Long id,
            @RequestBody ActionRequest request) {

        Expense expense = expenseService.reimburseExpense(id, request.getUserId());
        return ResponseEntity.ok(expense);
    }

    @GetMapping
    public ResponseEntity<List<Expense>> getAll() {
        return ResponseEntity.ok(expenseService.getAllExpenses());
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<Expense>> getByUser(@PathVariable Long id) {
        return ResponseEntity.ok(expenseService.getExpensesByUser(id));
    }
}

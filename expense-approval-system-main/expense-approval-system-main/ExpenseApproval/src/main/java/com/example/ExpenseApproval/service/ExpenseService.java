package com.example.ExpenseApproval.service;

import com.example.ExpenseApproval.exception.InvalidStateException;
import com.example.ExpenseApproval.exception.ResourceNotFoundException;
import com.example.ExpenseApproval.exception.UnauthorizedActionException;
import com.example.ExpenseApproval.model.*;
import com.example.ExpenseApproval.repository.AuditRepository;
import com.example.ExpenseApproval.repository.ExpenseRepository;
import com.example.ExpenseApproval.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final AuditRepository auditRepository;

    public ExpenseService(ExpenseRepository expenseRepository,
            UserRepository userRepository,
            AuditRepository auditRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
        this.auditRepository = auditRepository;
    }

    public Expense submitExpense(Long userId, BigDecimal amount, String description) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

if (user.getRole() != Role.EMPLOYEE) {
            throw new UnauthorizedActionException("Only employees can submit expenses.");
        }

        Expense expense = new Expense();
        expense.setAmount(amount);
        expense.setDescription(description);
        expense.setStatus(ExpenseStatus.SUBMITTED);
        expense.setSubmittedBy(user);

        return expenseRepository.save(expense);
    }

    public Expense approveExpense(Long expenseId, Long managerId) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + expenseId));

        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + managerId));

        if (manager.getRole() != Role.MANAGER) {
            throw new UnauthorizedActionException("Only managers can approve expenses.");
        }

        if (expense.getStatus() != ExpenseStatus.SUBMITTED) {
            throw new InvalidStateException(
                    "Expense must be in SUBMITTED state to be approved. Current state: " + expense.getStatus());
        }

        if (expense.getSubmittedBy().getId().equals(managerId)) {
            throw new UnauthorizedActionException("Managers cannot approve their own expenses.");
        }

        ExpenseStatus oldStatus = expense.getStatus();
        expense.setStatus(ExpenseStatus.APPROVED);
        expense.setApprovedBy(manager);

        saveAudit(expense, manager, oldStatus, ExpenseStatus.APPROVED);
        return expenseRepository.save(expense);
    }

    public Expense rejectExpense(Long expenseId, Long managerId) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + expenseId));

        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + managerId));

        if (manager.getRole() != Role.MANAGER) {
            throw new UnauthorizedActionException("Only managers can reject expenses.");
        }

        if (expense.getStatus() != ExpenseStatus.SUBMITTED) {
            throw new InvalidStateException(
                    "Expense must be in SUBMITTED state to be rejected. Current state: " + expense.getStatus());
        }

        if (expense.getSubmittedBy().getId().equals(managerId)) {
            throw new UnauthorizedActionException("Managers cannot reject their own expenses.");
        }

        ExpenseStatus oldStatus = expense.getStatus();
        expense.setStatus(ExpenseStatus.REJECTED);

        saveAudit(expense, manager, oldStatus, ExpenseStatus.REJECTED);
        return expenseRepository.save(expense);
    }

    public Expense reimburseExpense(Long expenseId, Long financeId) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + expenseId));

        User financeUser = userRepository.findById(financeId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + financeId));

        if (financeUser.getRole() != Role.FINANCE) {
            throw new UnauthorizedActionException("Only finance users can reimburse expenses.");
        }

        if (expense.getStatus() != ExpenseStatus.APPROVED) {
            throw new InvalidStateException(
                    "Expense must be in APPROVED state to be reimbursed. Current state: " + expense.getStatus());
        }

        ExpenseStatus oldStatus = expense.getStatus();
        expense.setStatus(ExpenseStatus.REIMBURSED);

        saveAudit(expense, financeUser, oldStatus, ExpenseStatus.REIMBURSED);
        return expenseRepository.save(expense);
    }

    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    public List<Expense> getExpensesByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        return expenseRepository.findBySubmittedBy(user);
    }

    private void saveAudit(Expense expense, User changedBy, ExpenseStatus fromStatus, ExpenseStatus toStatus) {
        ExpenseAudit audit = new ExpenseAudit();
        audit.setExpense(expense);
        audit.setChangedBy(changedBy);
        audit.setFromStatus(fromStatus.name());
        audit.setToStatus(toStatus.name());
        audit.setTimestamp(LocalDateTime.now());
        auditRepository.save(audit);
    }
}
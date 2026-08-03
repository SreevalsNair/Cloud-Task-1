package com.example.ExpenseApproval.controller;

import com.example.ExpenseApproval.dto.LoginRequest;
import com.example.ExpenseApproval.dto.LoginResponse;
import com.example.ExpenseApproval.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = {"http://13.232.7.225"})
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request.getName(), request.getRole(), request.getPassword()));
    }
}

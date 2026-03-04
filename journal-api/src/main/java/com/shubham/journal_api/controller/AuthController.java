package com.shubham.journal_api.controller;

import com.shubham.journal_api.dto.AuthResponse;
import com.shubham.journal_api.dto.LoginRequest;
import com.shubham.journal_api.dto.RegisterRequest;
import com.shubham.journal_api.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request){
        try{
            AuthResponse response = authService.register(request);
            return new ResponseEntity<>(response,HttpStatus.CREATED);
        } catch (RuntimeException e ){
            return new ResponseEntity<>(
                    new AuthResponse(e.getMessage(), null),
            HttpStatus.BAD_REQUEST
            );
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request){
        try{
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(
                    new AuthResponse(e.getMessage(),null),
                    HttpStatus.UNAUTHORIZED
            );
        }
    }

    @GetMapping("/test")
    public String test() {
        return "Auth controller is working!";
    }


}

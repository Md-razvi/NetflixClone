package com.netflix.clone.controller;

import com.netflix.clone.dto.request.EmailRequest;
import com.netflix.clone.dto.request.LoginRequest;
import com.netflix.clone.dto.request.ResetPasswordRequest;
import com.netflix.clone.dto.request.UserRequest;
import com.netflix.clone.dto.response.EmailValidation;
import com.netflix.clone.dto.response.LoginResponse;
import com.netflix.clone.dto.response.MessageResponse;
import com.netflix.clone.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<MessageResponse>register(@Valid @RequestBody UserRequest userRequest){
        return  ResponseEntity.ok(authService.signup(userRequest));
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponse>login(@Valid @RequestBody LoginRequest loginRequest){
        LoginResponse loginResponse=authService.login(loginRequest.getEmail(),loginRequest.getPassword());
        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/validate-email")
    public ResponseEntity<EmailValidation> validateEmail(@RequestParam String email){
        return ResponseEntity.ok(authService.validateEmail(email));
    }
    @GetMapping("/verify-email")
    public ResponseEntity<MessageResponse> verifyEmail(@RequestParam String token){
        return ResponseEntity.ok(authService.verifyEmail(token));
    }
    @PostMapping("/resend-verification")
    public ResponseEntity<MessageResponse>resendVerification(@Valid @RequestBody EmailRequest emailRequest){
        return ResponseEntity.ok(authService.resendVerificationEmail(emailRequest.getEmail()));
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse>forgotPassword(@Valid @RequestBody EmailRequest emailRequest){
        return ResponseEntity.ok(authService.forgotPassword(emailRequest.getEmail()));
    }
    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse>resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest){
        return ResponseEntity.ok(authService.resetPassword(resetPasswordRequest.getToken(),resetPasswordRequest.getNewPassword()));
    }
}

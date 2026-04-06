package com.netflix.clone.controller;

import com.netflix.clone.dto.request.LoginRequest;
import com.netflix.clone.dto.request.UserRequest;
import com.netflix.clone.dto.response.LoginResponse;
import com.netflix.clone.dto.response.MessageResponse;
import com.netflix.clone.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

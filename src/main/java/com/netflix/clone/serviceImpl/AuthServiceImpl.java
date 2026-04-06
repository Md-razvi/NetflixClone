package com.netflix.clone.serviceImpl;

import com.netflix.clone.dto.request.UserRequest;
import com.netflix.clone.dto.response.MessageResponse;
import com.netflix.clone.repository.UserRepository;
import com.netflix.clone.security.JwtUtil;
import com.netflix.clone.service.AuthService;
import com.netflix.clone.service.EmailService;
import com.netflix.clone.util.ServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ServiceUtil serviceUtil;

    @Override
    public MessageResponse signup(UserRequest userRequest) {

        return new MessageResponse("Registration Successful! Please check your email to verify your account!");
    }
}

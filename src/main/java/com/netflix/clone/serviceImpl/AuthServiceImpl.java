package com.netflix.clone.serviceImpl;

import com.netflix.clone.dto.request.UserRequest;
import com.netflix.clone.dto.response.LoginResponse;
import com.netflix.clone.dto.response.MessageResponse;
import com.netflix.clone.entity.User;
import com.netflix.clone.enums.Role;
import com.netflix.clone.exceptions.AccountDeactivationException;
import com.netflix.clone.exceptions.BadCredentialException;
import com.netflix.clone.exceptions.EmailAlreadyExist;
import com.netflix.clone.exceptions.EmailNotVerifiedException;
import com.netflix.clone.repository.UserRepository;
import com.netflix.clone.security.JwtUtil;
import com.netflix.clone.service.AuthService;
import com.netflix.clone.service.EmailService;
import com.netflix.clone.util.ServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
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

    // ✅ Signup Method
    @Override
    @Transactional
    public MessageResponse signup(UserRequest userRequest) {

        // Check if email already exists
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new EmailAlreadyExist("The given email is already present in the db");
        }

        // Create new user
        User user = new User();
        user.setEmail(userRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword())); // ✅ ENCODE PASSWORD
        user.setFullName(userRequest.getFullName());
        user.setRole(Role.USER);
        user.setActive(true);
        user.setEmailVerified(false);

        // Generate verification token
        String verificationToken = UUID.randomUUID().toString();
        user.setVerificatonToken(verificationToken); // ✅ FIXED TYPO
        user.setVerificationTokenExpiry(Instant.now().plusSeconds(3600)); // ✅ 1 hour expiry

        // Save user FIRST
        userRepository.save(user);

        // Send verification email
        emailService.sendVerificationEmail(user.getEmail(), verificationToken);

        return new MessageResponse(
                "Registration Successful! Please check your email to verify your account!"
        );
    }

    // ✅ Login Method
    @Override
    public LoginResponse login(String email, String password) {

        // Find user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialException("Invalid email or password"));

        // Check password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialException("Invalid email or password");
        }

        // Check if account is active
        if (!user.isActive()) {
            throw new AccountDeactivationException("Your account has been deactivated");
        }

        // Check if email is verified
        if (!user.isEmailVerified()) {
            throw new EmailNotVerifiedException("Please verify your email before login");
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        // Return response
        return new LoginResponse(
                token,
                user.getEmail(),
                user.getFullName(),
                user.getRole().name()
        );
    }
}
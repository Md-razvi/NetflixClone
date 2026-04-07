package com.netflix.clone.serviceImpl;

import com.netflix.clone.dto.request.UserRequest;
import com.netflix.clone.dto.response.EmailValidation;
import com.netflix.clone.dto.response.LoginResponse;
import com.netflix.clone.dto.response.MessageResponse;
import com.netflix.clone.entity.User;
import com.netflix.clone.enums.Role;
import com.netflix.clone.exceptions.*;
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
        user.setVerificationToken(verificationToken); // ✅ FIXED TYPO
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

    @Override
    public EmailValidation validateEmail(String email) {
            boolean exist=userRepository.existsByEmail(email);
            return new EmailValidation(exist,!exist);
    }

    @Override
    public MessageResponse verifyEmail(String verificationToken) {
        User user=userRepository.findByVerificationToken(verificationToken)
                .orElseThrow(()->new InvalidTokenException("Invalid or expired token"));
        if(user.getVerificationTokenExpiry()==null
                || user.getVerificationTokenExpiry().isBefore(Instant.now())){
            throw new InvalidTokenException("Verification has been expired .PLease request a new one");
        }
        user.setEmailVerified(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);
        userRepository.save(user);

        return new MessageResponse("Email has been verified.You can now login");
    }

    @Override
    public MessageResponse resendVerificationEmail(String email) {
        User user=serviceUtil.getByEmailOrThrow(email);
        String verificationToken=UUID.randomUUID().toString();
        user.setVerificationToken(verificationToken);
        user.setVerificationTokenExpiry(Instant.now().plusSeconds(86400));
        userRepository.save(user);
        emailService.sendVerificationEmail(email,verificationToken);

        return new MessageResponse("Verification resent  successfully ! please check your email for further process");

    }

    @Override
    public MessageResponse forgotPassword(String email) {
        User user =serviceUtil.getByEmailOrThrow(email);
        String resetToken=UUID.randomUUID().toString();
        user.setPasswordResetToken(resetToken);
        user.setPasswordResetTokenExpiry(Instant.now().plusSeconds(3600));
        userRepository.save(user);
        emailService.sendPasswordResetEmail(email,resetToken);
        return new MessageResponse("Password Reset Email sent to the given user email");
    }

    @Override
    public MessageResponse resetPassword(String token, String newPassword) {
        User user=userRepository.findByPasswordResetToken(token)
                .orElseThrow(()->new InvalidTokenException("Invalid or expired  token exception"));
        if(user.getPasswordResetToken()==null || user.getPasswordResetTokenExpiry().isBefore(Instant.now())){
        throw new InvalidTokenException("Reset token has been expired");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiry(null);
        userRepository.save(user);
        return new MessageResponse("Password has been Changed or reset successfully!");
    }
}
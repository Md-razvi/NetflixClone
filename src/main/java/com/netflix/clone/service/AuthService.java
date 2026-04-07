package com.netflix.clone.service;

import com.netflix.clone.dto.request.UserRequest;
import com.netflix.clone.dto.response.EmailValidation;
import com.netflix.clone.dto.response.LoginResponse;
import com.netflix.clone.dto.response.MessageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.Nullable;

public interface AuthService {
    MessageResponse signup(@Valid UserRequest userRequest);
    LoginResponse login(String email,String password);
    EmailValidation validateEmail(String email);
    MessageResponse verifyEmail(String verificationToken);
    MessageResponse resendVerificationEmail(String email);
    MessageResponse forgotPassword(String email);
    MessageResponse resetPassword(String token,String newPassword);
}

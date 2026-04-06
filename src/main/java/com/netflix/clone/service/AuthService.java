package com.netflix.clone.service;

import com.netflix.clone.dto.request.UserRequest;
import com.netflix.clone.dto.response.LoginResponse;
import com.netflix.clone.dto.response.MessageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.jspecify.annotations.Nullable;

public interface AuthService {
    MessageResponse signup(@Valid UserRequest userRequest);
    LoginResponse login(String email,String password);

}

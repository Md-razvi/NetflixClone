package com.netflix.clone.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPassword {
    @NotBlank
    private String token;
    @NotBlank
    @Size(min=7, message="New Password must be atleast 6 characters long")
    private String password;
}

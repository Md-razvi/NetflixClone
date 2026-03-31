package com.netflix.clone.dto.request;

import lombok.Data;

@Data
public class UserRequest {
    private String email;
    private String password;
    private String role;
    private Boolean isActive;

}

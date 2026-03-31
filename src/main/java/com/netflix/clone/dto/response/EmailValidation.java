package com.netflix.clone.dto.response;

//import jakarta.persistence.GeneratedValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmailValidation {
    private boolean exists;
    private boolean available;
}

package com.netflix.clone.exceptions;

public class BadCredentialException extends RuntimeException{
    public BadCredentialException(String message){
        super(message);
    }
}

package com.netflix.clone.exceptions;

//import org.springframework.stereotype.Controller;
import org.apache.catalina.connector.ClientAbortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;

import java.time.Instant;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler{
    public static final Logger log= LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BadCredentialException.class)
    public ResponseEntity<Map<String,Object>> handleBadCredential(BadCredentialException ex){
        log.warn("Bad Credential Exception: {}",ex.getMessage(),ex);
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }
    @ExceptionHandler(AccountDeactivationException.class)
    public ResponseEntity<Map<String,Object>> handleAccountDeactivation(AccountDeactivationException ex){
        log.warn("AccountDeactivationException: {}",ex.getMessage(),ex);
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage());
    }
    @ExceptionHandler(EmailNotVerifiedException.class)
    public ResponseEntity<Map<String,Object>> handleEmailNotVerified(EmailNotVerifiedException ex){
        log.warn("EmailNotVerifiedException: {}",ex.getMessage(),ex);
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage());
    }
    @ExceptionHandler(EmailSendingException.class)
    public ResponseEntity<Map<String,Object>> handleEmailSending(EmailSendingException ex){
        log.warn("EmailSendingException: {}",ex.getMessage(),ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }
    @ExceptionHandler(InvalidCredentialsExceptions.class)
    public ResponseEntity<Map<String,Object>> handleInvalidCredentials(InvalidCredentialsExceptions ex){
        log.warn("InvalidCredentialsExceptions: {}",ex.getMessage(),ex);
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
    @ExceptionHandler(InvalidRoleException.class)
    public ResponseEntity<Map<String,Object>> handleInvalidRole(InvalidRoleException ex){
        log.warn("InvalidRoleException: {}",ex.getMessage(),ex);
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<Map<String,Object>> handleInvalidToken(InvalidTokenException ex){
        log.warn("InvalidTokenException: {}",ex.getMessage(),ex);
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String,Object>> handleResourceNotFound(ResourceNotFoundException ex){
        log.warn("ResourceNotFoundException: {}",ex.getMessage(),ex);
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }
    @ExceptionHandler(EmailAlreadyExist.class)
    public ResponseEntity<Map<String,Object>> handleEmailAlreadyExist(EmailAlreadyExist ex){
        log.warn("EmailAlreadyExistException: {}",ex.getMessage(),ex);
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,Object>> handleValidationException(MethodArgumentNotValidException ex){
        String message=ex.getBindingResult().getFieldErrors().
                stream().findFirst().map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .orElse("Invalid Request");
        HttpStatus st=HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(st).body(Map.of("timestamp",Instant.now(),"status",st.value(),"error",message));
    }

    @ExceptionHandler({AsyncRequestNotUsableException.class, ClientAbortException.class})
    public void handleClientAbort(Exception ex){
        log.debug(
                "Client closed connection during streaming (expected for video seeking/buffering {}",
                ex.getMessage()
                );
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,Object>> handleGeneric(Exception ex){
        log.warn("Exception: {}",ex.getMessage(),ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }





    public ResponseEntity<Map<String,Object>>buildResponse(HttpStatus status, String message){
        Map<String,Object> body=Map.of("timestamp", Instant.now(),"error",message);
        return ResponseEntity.status(status).body(body);
    }

}

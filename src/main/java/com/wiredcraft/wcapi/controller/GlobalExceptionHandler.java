package com.wiredcraft.wcapi.controller;

import com.wiredcraft.wcapi.exception.UserRegistrationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserRegistrationException.class)
    public ResponseEntity<Map<String, String>> handleCustomException(UserRegistrationException ex) {
        return prepareResponse(ex.getMessage(), String.valueOf(HttpStatus.BAD_REQUEST.value()));
    }

    private ResponseEntity<Map<String, String>> prepareResponse(String error, String status) {
        Map<String, String> response = new HashMap<>();
        response.put("error", error);
        response.put("status", status);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}

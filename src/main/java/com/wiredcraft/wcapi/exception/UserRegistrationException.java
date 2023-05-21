package com.wiredcraft.wcapi.exception;

public class UserRegistrationException extends RuntimeException {
    public UserRegistrationException(String s) {
        super(s);
    }

    public UserRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }
}

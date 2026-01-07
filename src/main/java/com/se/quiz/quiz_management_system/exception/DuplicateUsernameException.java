package com.se.quiz.quiz_management_system.exception;

    // Exception thrown when attempting to register with a username that already exists
    
public class DuplicateUsernameException extends RuntimeException {
    
    public DuplicateUsernameException(String message) {
        super(message);
    }
    
    public DuplicateUsernameException(String message, Throwable cause) {
        super(message, cause);
    }
}


package com.fawry.lms.shared.errors;

public class ConflictError extends RuntimeException {
    public ConflictError(String message) {
        super(message);
    }
    
}
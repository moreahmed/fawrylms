package com.fawry.lms.shared.errors;

public class NotFoundError extends RuntimeException {
    public NotFoundError(String message) {
        super(message);
    }
    
}

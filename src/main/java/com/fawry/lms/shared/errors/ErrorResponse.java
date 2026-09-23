package com.fawry.lms.shared.errors;

public record ErrorResponse (
    String code,
    String message
) {
    
}

package com.fawry.lms.auth;

public record LoginRequest (
    String username,
    String password
) {
    
}

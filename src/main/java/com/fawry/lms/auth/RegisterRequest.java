package com.fawry.lms.auth;

public record RegisterRequest (
    String username,
    String password
) {}
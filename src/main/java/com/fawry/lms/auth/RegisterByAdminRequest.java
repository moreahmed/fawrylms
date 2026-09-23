package com.fawry.lms.auth;

public record RegisterByAdminRequest (
    String username,
    String password,
    Role role
) {}
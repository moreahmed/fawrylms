package com.fawry.lms.students;

import com.fawry.lms.auth.User;

public record StudentResponse(Long id, String username) {
    public static StudentResponse from(User user) {
        return new StudentResponse(user.getId(), user.getUsername());
    }
}

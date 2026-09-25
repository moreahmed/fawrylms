package com.fawry.lms.enrollments;

import java.time.LocalDateTime;

public record EnrollmentResponse(
    Long id,
    Long studentId,
    String studentUsername,
    Long courseId,
    String courseCode,
    String courseName,
    int hours,
    String semesterName,
    int attemptNumber,
    EnrollmentStatus status,
    LocalDateTime enrolledAt,
    LocalDateTime completedAt
) {
    public static EnrollmentResponse from(Enrollment enrollment) {
        return new EnrollmentResponse(
            enrollment.getId(), enrollment.getStudent().getId(), enrollment.getStudent().getUsername(),
            enrollment.getCourse().getId(), enrollment.getCourse().getCode(), enrollment.getCourse().getName(),
            enrollment.getCourse().getHours(), enrollment.getSemesterName(), enrollment.getAttemptNumber(),
            enrollment.getStatus(), enrollment.getEnrolledAt(), enrollment.getCompletedAt()
        );
    }
}

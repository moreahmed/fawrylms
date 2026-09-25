package com.fawry.lms.semester;

import java.time.LocalDate;

public record SemesterSettingsRequest(
    String semesterName,
    LocalDate startsAt,
    LocalDate endsAt,
    Integer maxHours,
    Integer maxFailedAttempts
) {}

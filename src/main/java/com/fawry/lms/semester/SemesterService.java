package com.fawry.lms.semester;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fawry.lms.shared.errors.BadRequestError;
import com.fawry.lms.shared.errors.ConflictError;
import com.fawry.lms.shared.errors.NotFoundError;

@Service
public class SemesterService {
    private final SemesterSettingsRepository semesterSettingsRepository;

    public SemesterService(SemesterSettingsRepository semesterSettingsRepository) {
        this.semesterSettingsRepository = semesterSettingsRepository;
    }

    public List<SemesterSettings> getSemesters() {
        return semesterSettingsRepository.findAll();
    }

    @Transactional
    public SemesterSettings getActiveSemester() {
        LocalDate today = LocalDate.now();
        return semesterSettingsRepository
            .findFirstByStartsAtLessThanEqualAndEndsAtGreaterThanEqualOrderByStartsAtDesc(today, today)
            .orElseThrow(() -> new ConflictError("no semester is currently active"));
    }

    @Transactional
    public SemesterSettings createSemester(SemesterSettingsRequest request) {
        validate(request);
        ensureNoOverlap(request, null);
        return semesterSettingsRepository.save(toSemester(request, new SemesterSettings()));
    }

    @Transactional
    public SemesterSettings updateSemester(Long id, SemesterSettingsRequest request) {
        validate(request);
        SemesterSettings semester = semesterSettingsRepository.findById(id)
            .orElseThrow(() -> new NotFoundError("semester not found"));
        ensureNoOverlap(request, id);
        return semesterSettingsRepository.save(toSemester(request, semester));
    }

    private void validate(SemesterSettingsRequest request) {
        if (request.semesterName() == null || request.semesterName().isBlank()
            || request.startsAt() == null || request.endsAt() == null || request.maxHours() == null
            || request.maxFailedAttempts() == null || request.maxHours() < 1 || request.maxFailedAttempts() < 1
            || request.startsAt().isAfter(request.endsAt())) {
            throw new BadRequestError("provide a semester name, valid dates, positive max hours, and positive max failed attempts");
        }
    }

    private void ensureNoOverlap(SemesterSettingsRequest request, Long excludedId) {
        boolean overlaps = semesterSettingsRepository
            .findAllByStartsAtLessThanEqualAndEndsAtGreaterThanEqual(request.endsAt(), request.startsAt())
            .stream()
            .anyMatch(semester -> !semester.getId().equals(excludedId));
        if (overlaps) {
            throw new ConflictError("semester dates overlap an existing semester");
        }
    }

    private SemesterSettings toSemester(SemesterSettingsRequest request, SemesterSettings semester) {
        semester.setSemesterName(request.semesterName().trim());
        semester.setStartsAt(request.startsAt());
        semester.setEndsAt(request.endsAt());
        semester.setMaxHours(request.maxHours());
        semester.setMaxFailedAttempts(request.maxFailedAttempts());
        return semester;
    }
}

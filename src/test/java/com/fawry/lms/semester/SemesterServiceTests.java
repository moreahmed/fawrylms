package com.fawry.lms.semester;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fawry.lms.shared.errors.ConflictError;

class SemesterServiceTests {
    private SemesterSettingsRepository semesterSettingsRepository;
    private SemesterService semesterService;

    @BeforeEach
    void setUp() {
        semesterSettingsRepository = mock(SemesterSettingsRepository.class);
        semesterService = new SemesterService(semesterSettingsRepository);
    }

    @Test
    void rejectsSemesterWithOverlappingDatesIncludingSharedBoundary() {
        LocalDate startsAt = LocalDate.of(2026, 9, 1);
        LocalDate endsAt = LocalDate.of(2027, 1, 15);
        SemesterSettings existing = mock(SemesterSettings.class);
        when(existing.getId()).thenReturn(1L);
        when(semesterSettingsRepository.findAllByStartsAtLessThanEqualAndEndsAtGreaterThanEqual(
            endsAt, startsAt)).thenReturn(List.of(existing));

        assertThrows(ConflictError.class, () -> semesterService.createSemester(
            new SemesterSettingsRequest("Fall 2026", startsAt, endsAt, 18, 2)));
    }

    @Test
    void createsSemesterWhenDatesDoNotOverlap() {
        LocalDate startsAt = LocalDate.of(2027, 2, 1);
        LocalDate endsAt = LocalDate.of(2027, 6, 15);
        when(semesterSettingsRepository.findAllByStartsAtLessThanEqualAndEndsAtGreaterThanEqual(
            endsAt, startsAt)).thenReturn(List.of());
        when(semesterSettingsRepository.save(any(SemesterSettings.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        SemesterSettings created = semesterService.createSemester(
            new SemesterSettingsRequest("Spring 2027", startsAt, endsAt, 18, 2));

        assertEquals("Spring 2027", created.getSemesterName());
        assertEquals(startsAt, created.getStartsAt());
        assertEquals(endsAt, created.getEndsAt());
    }
}

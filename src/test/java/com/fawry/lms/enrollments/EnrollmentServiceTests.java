package com.fawry.lms.enrollments;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fawry.lms.auth.Role;
import com.fawry.lms.auth.User;
import com.fawry.lms.auth.UserRepository;
import com.fawry.lms.courses.Course;
import com.fawry.lms.courses.CourseRepository;
import com.fawry.lms.semester.SemesterSettings;
import com.fawry.lms.semester.SemesterSettingsRepository;
import com.fawry.lms.shared.errors.ConflictError;

class EnrollmentServiceTests {
    private UserRepository userRepository;
    private CourseRepository courseRepository;
    private EnrollmentRepository enrollmentRepository;
    private SemesterSettingsRepository semesterSettingsRepository;
    private EnrollmentService enrollmentService;
    private User student;
    private Course course;
    private SemesterSettings settings;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        courseRepository = mock(CourseRepository.class);
        enrollmentRepository = mock(EnrollmentRepository.class);
        semesterSettingsRepository = mock(SemesterSettingsRepository.class);
        enrollmentService = new EnrollmentService(
            userRepository, courseRepository, enrollmentRepository, semesterSettingsRepository);

        student = mock(User.class);
        when(student.getId()).thenReturn(41L);
        when(student.getUsername()).thenReturn("student");
        when(student.getRole()).thenReturn(Role.STUDENT);
        course = mock(Course.class);
        when(course.getHours()).thenReturn(3);
        when(course.getPrerequisite()).thenReturn(null);

        settings = new SemesterSettings();
        settings.setSemesterName("Fall 2026");
        settings.setStartsAt(LocalDate.now().minusDays(1));
        settings.setEndsAt(LocalDate.now().plusDays(30));
        settings.setMaxHours(18);
        settings.setMaxFailedAttempts(2);

        when(userRepository.findByUsername("student")).thenReturn(Optional.of(student));
        when(courseRepository.findById(7L)).thenReturn(Optional.of(course));
        when(semesterSettingsRepository
            .findFirstByStartsAtLessThanEqualAndEndsAtGreaterThanEqualOrderByStartsAtDesc(
                any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(Optional.of(settings));
    }

    @Test
    void enrollsStudentInEligibleCourse() {
        when(enrollmentRepository.findByStudentAndCourse(student, course)).thenReturn(List.of());
        when(enrollmentRepository.findByStudentAndSemesterAndStatus(
            eq(student), eq(settings), eq(EnrollmentStatus.ENROLLED))).thenReturn(List.of());
        when(enrollmentRepository.save(any(Enrollment.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        EnrollmentResponse result = enrollmentService.enroll("student", 7L);

        assertEquals(EnrollmentStatus.ENROLLED, result.status());
        assertEquals(1, result.attemptNumber());
        assertEquals("Fall 2026", result.semesterName());
    }

    @Test
    void rejectsCourseWithUncompletedPrerequisite() {
        Course prerequisite = mock(Course.class);
        when(course.getPrerequisite()).thenReturn(prerequisite);
        when(enrollmentRepository.findByStudentAndCourse(eq(student), any(Course.class))).thenReturn(List.of());

        assertThrows(ConflictError.class, () -> enrollmentService.enroll("student", 7L));
    }

    @Test
    void rejectsEnrollmentWhenNoSemesterIsActive() {
        when(semesterSettingsRepository
            .findFirstByStartsAtLessThanEqualAndEndsAtGreaterThanEqualOrderByStartsAtDesc(
                any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(Optional.empty());

        assertThrows(ConflictError.class, () -> enrollmentService.enroll("student", 7L));
    }

    @Test
    void rejectsCourseAfterFailureLimitIsReached() {
        Enrollment firstFailure = mock(Enrollment.class);
        Enrollment secondFailure = mock(Enrollment.class);
        when(firstFailure.getStatus()).thenReturn(EnrollmentStatus.FAILED);
        when(secondFailure.getStatus()).thenReturn(EnrollmentStatus.FAILED);
        when(enrollmentRepository.findByStudentAndCourse(student, course))
            .thenReturn(List.of(firstFailure, secondFailure));

        assertThrows(ConflictError.class, () -> enrollmentService.enroll("student", 7L));
    }

    @Test
    void rejectsCourseAlreadyCompleted() {
        Enrollment completed = mock(Enrollment.class);
        when(completed.getStatus()).thenReturn(EnrollmentStatus.COMPLETED);
        when(enrollmentRepository.findByStudentAndCourse(student, course)).thenReturn(List.of(completed));

        assertThrows(ConflictError.class, () -> enrollmentService.enroll("student", 7L));
    }

    @Test
    void rejectsEnrollmentThatWouldExceedSemesterHours() {
        Course existingCourse = mock(Course.class);
        when(existingCourse.getHours()).thenReturn(16);
        Enrollment existingEnrollment = mock(Enrollment.class);
        when(existingEnrollment.getCourse()).thenReturn(existingCourse);
        when(enrollmentRepository.findByStudentAndCourse(student, course)).thenReturn(List.of());
        when(enrollmentRepository.findByStudentAndSemesterAndStatus(
            eq(student), eq(settings), eq(EnrollmentStatus.ENROLLED)))
            .thenReturn(List.of(existingEnrollment));

        assertThrows(ConflictError.class, () -> enrollmentService.enroll("student", 7L));
    }

    @Test
    void dropsOwnActiveEnrollment() {
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setSemesterName("Fall 2026");
        enrollment.setAttemptNumber(1);
        enrollment.setStatus(EnrollmentStatus.ENROLLED);
        when(enrollmentRepository.findById(11L)).thenReturn(Optional.of(enrollment));
        when(enrollmentRepository.save(any(Enrollment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EnrollmentResponse result = enrollmentService.drop("student", 11L);

        assertEquals(EnrollmentStatus.DROPPED, result.status());
    }

}

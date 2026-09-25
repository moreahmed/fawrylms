package com.fawry.lms.enrollments;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fawry.lms.auth.Role;
import com.fawry.lms.auth.User;
import com.fawry.lms.auth.UserRepository;
import com.fawry.lms.courses.Course;
import com.fawry.lms.courses.CourseRepository;
import com.fawry.lms.semester.SemesterSettings;
import com.fawry.lms.semester.SemesterSettingsRepository;
import com.fawry.lms.shared.errors.BadRequestError;
import com.fawry.lms.shared.errors.ConflictError;
import com.fawry.lms.shared.errors.ForbiddenError;
import com.fawry.lms.shared.errors.NotFoundError;

@Service
public class EnrollmentService {
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final SemesterSettingsRepository semesterSettingsRepository;

    public EnrollmentService(UserRepository userRepository, CourseRepository courseRepository,
        EnrollmentRepository enrollmentRepository, SemesterSettingsRepository semesterSettingsRepository) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.semesterSettingsRepository = semesterSettingsRepository;
    }

    public List<EnrollmentResponse> getEnrollments(String username) {
        return enrollmentRepository.findByStudentOrderByEnrolledAtDesc(getStudentByUsername(username)).stream()
            .map(EnrollmentResponse::from).toList();
    }

    public List<EnrollmentResponse> getEnrollments(Long studentId) {
        return enrollmentRepository.findByStudentOrderByEnrolledAtDesc(getStudent(studentId)).stream()
            .map(EnrollmentResponse::from).toList();
    }

    @Transactional
    public EnrollmentResponse enroll(String username, Long courseId) {
        User student = getStudentByUsername(username);
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new NotFoundError("course not found"));
        SemesterSettings settings = getActiveSemester();

        List<Enrollment> history = enrollmentRepository.findByStudentAndCourse(student, course);
        if (history.stream().anyMatch(item -> item.getStatus() == EnrollmentStatus.COMPLETED)) {
            throw new ConflictError("course has already been completed");
        }
        if (history.stream().anyMatch(item -> item.getStatus() == EnrollmentStatus.ENROLLED)) {
            throw new ConflictError("student is already enrolled in this course");
        }
        long failedAttempts = history.stream().filter(item -> item.getStatus() == EnrollmentStatus.FAILED).count();
        if (failedAttempts >= settings.getMaxFailedAttempts()) {
            throw new ConflictError("maximum failed attempts reached for this course");
        }

        Course prerequisite = course.getPrerequisite();
        if (prerequisite != null && enrollmentRepository.findByStudentAndCourse(student, prerequisite).stream()
            .noneMatch(item -> item.getStatus() == EnrollmentStatus.COMPLETED)) {
            throw new ConflictError("course prerequisite has not been completed");
        }

        List<Enrollment> active = enrollmentRepository.findByStudentAndSemesterAndStatus(
            student, settings, EnrollmentStatus.ENROLLED);
        int registeredHours = active.stream().mapToInt(item -> item.getCourse().getHours()).sum();
        if (registeredHours + course.getHours() > settings.getMaxHours()) {
            throw new ConflictError("maximum registered hours for the semester would be exceeded");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setSemester(settings);
        enrollment.setSemesterName(settings.getSemesterName());
        enrollment.setAttemptNumber((int) failedAttempts + 1);
        enrollment.setStatus(EnrollmentStatus.ENROLLED);
        enrollment.setEnrolledAt(LocalDateTime.now());
        return EnrollmentResponse.from(enrollmentRepository.save(enrollment));
    }

    @Transactional
    public EnrollmentResponse drop(String username, Long enrollmentId) {
        User student = getStudentByUsername(username);
        Enrollment enrollment = getEnrollment(enrollmentId);
        if (!enrollment.getStudent().getId().equals(student.getId())) {
            throw new ForbiddenError("cannot drop another student's enrollment");
        }
        if (enrollment.getStatus() != EnrollmentStatus.ENROLLED) {
            throw new ConflictError("only active enrollments can be dropped");
        }
        enrollment.setStatus(EnrollmentStatus.DROPPED);
        enrollment.setCompletedAt(LocalDateTime.now());
        return EnrollmentResponse.from(enrollmentRepository.save(enrollment));
    }

    @Transactional
    public EnrollmentResponse recordResult(Long enrollmentId, EnrollmentStatus status) {
        if (status != EnrollmentStatus.COMPLETED && status != EnrollmentStatus.FAILED) {
            throw new BadRequestError("result must be COMPLETED or FAILED");
        }
        Enrollment enrollment = getEnrollment(enrollmentId);
        if (enrollment.getStatus() != EnrollmentStatus.ENROLLED) {
            throw new ConflictError("result can only be recorded for an active enrollment");
        }
        enrollment.setStatus(status);
        enrollment.setCompletedAt(LocalDateTime.now());
        return EnrollmentResponse.from(enrollmentRepository.save(enrollment));
    }

    private User getStudentByUsername(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new NotFoundError("student not found"));
        if (user.getRole() != Role.STUDENT) throw new ForbiddenError("student account required");
        return user;
    }

    private User getStudent(Long studentId) {
        User user = userRepository.findById(studentId).orElseThrow(() -> new NotFoundError("student not found"));
        if (user.getRole() != Role.STUDENT) throw new NotFoundError("student not found");
        return user;
    }

    private Enrollment getEnrollment(Long enrollmentId) {
        return enrollmentRepository.findById(enrollmentId)
            .orElseThrow(() -> new NotFoundError("enrollment not found"));
    }

    private SemesterSettings getActiveSemester() {
        LocalDate today = LocalDate.now();
        return semesterSettingsRepository
            .findFirstByStartsAtLessThanEqualAndEndsAtGreaterThanEqualOrderByStartsAtDesc(today, today)
            .orElseThrow(() -> new ConflictError("no semester is currently active"));
    }
}

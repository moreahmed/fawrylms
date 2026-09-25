package com.fawry.lms.enrollments;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/students")
public class EnrollmentController {
    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) { this.enrollmentService = enrollmentService; }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/me/enrollments")
    public List<EnrollmentResponse> getMyEnrollments(Authentication authentication) {
        return enrollmentService.getEnrollments(authentication.getName());
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/me/enrollments/{courseId}")
    public EnrollmentResponse enroll(Authentication authentication, @PathVariable Long courseId) {
        return enrollmentService.enroll(authentication.getName(), courseId);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @DeleteMapping("/me/enrollments/{enrollmentId}")
    public EnrollmentResponse drop(Authentication authentication, @PathVariable Long enrollmentId) {
        return enrollmentService.drop(authentication.getName(), enrollmentId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{studentId}/enrollments")
    public List<EnrollmentResponse> getStudentEnrollments(@PathVariable Long studentId) {
        return enrollmentService.getEnrollments(studentId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/enrollments/{enrollmentId}/result")
    public EnrollmentResponse recordResult(@PathVariable Long enrollmentId, @RequestBody EnrollmentResultRequest request) {
        return enrollmentService.recordResult(enrollmentId, request.status());
    }
}

package com.fawry.lms.enrollments;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fawry.lms.auth.User;
import com.fawry.lms.courses.Course;
import com.fawry.lms.semester.SemesterSettings;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudentOrderByEnrolledAtDesc(User student);
    List<Enrollment> findByStudentAndCourse(User student, Course course);
    List<Enrollment> findByStudentAndSemesterAndStatus(
        User student, SemesterSettings semester, EnrollmentStatus status);
}

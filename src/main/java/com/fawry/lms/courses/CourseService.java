package com.fawry.lms.courses;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fawry.lms.instructors.Instructor;
import com.fawry.lms.instructors.InstructorRepository;
import com.fawry.lms.shared.errors.NotFoundError;

@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final InstructorRepository instructorRepository;

    public CourseService(CourseRepository courseRepository, InstructorRepository instructorRepository) {
        this.courseRepository = courseRepository;
        this.instructorRepository = instructorRepository;
    }

    public List<Course> getCourses() {
        return courseRepository.findAll();
    }

    public Course getCourse(Long id) {
        return courseRepository.findById(id)
            .orElseThrow(() -> new NotFoundError("course not found"));
    }

    public Course createCourse(CourseRequest request) {
        Course course = new Course();
        updateCourse(course, request);
        return courseRepository.save(course);
    }

    public Course updateCourse(Long id, CourseRequest request) {
        Course course = getCourse(id);
        updateCourse(course, request);
        return courseRepository.save(course);
    }

    public void deleteCourse(Long id) {
        Course course = getCourse(id);
        courseRepository.delete(course);
    }

    private void updateCourse(Course course, CourseRequest request) {
        course.setName(request.name);
        course.setCode(request.code);
        course.setIssuedAt(request.issuedAt);
        course.setPrerequisite(request.prerequisiteId == null ? null : getCourse(request.prerequisiteId));

        if (request.instructorIds == null) {
            course.setInstructors(List.of());
            return;
        }

        List<Instructor> instructors = request.instructorIds.stream()
            .map(id -> instructorRepository.findById(id)
                .orElseThrow(() -> new NotFoundError("instructor not found")))
            .toList();
        course.setInstructors(instructors);
    }
}
package com.fawry.lms.instructors;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fawry.lms.department.Department;
import com.fawry.lms.department.DepartmentRepository;
import com.fawry.lms.shared.errors.NotFoundError;

@Service 
public class InstructorService {
    private final InstructorRepository instructorRepository;
    private final DepartmentRepository departmentRepository;

    public InstructorService(
        InstructorRepository instructorRepository,
        DepartmentRepository departmentRepository
    ) {
        this.instructorRepository = instructorRepository;
        this.departmentRepository = departmentRepository;
    }

    public List<Instructor> getInstructors() {
        return instructorRepository.findAll();
    }

    public Instructor getInstructor(Long id) {
        return instructorRepository.findById(id)
            .orElseThrow(() -> new NotFoundError("instructor not found"));
    }

    public Instructor createInstructor(AddInstructorRequest request) {
        Instructor instructor = new Instructor();
        instructor.setName(request.name);
        instructor.setDepartment(getDepartment(request.departmentId));
        return instructorRepository.save(instructor);
    }

    public Instructor updateInstructor(Long id, AddInstructorRequest request) {
        Instructor instructor = getInstructor(id);
        instructor.setName(request.name);
        instructor.setDepartment(getDepartment(request.departmentId));
        return instructorRepository.save(instructor);
    }

    public void deleteInstructor(Long id) {
        instructorRepository.delete(getInstructor(id));
    }

    private Department getDepartment(Long id) {
        return departmentRepository.findById(id)
            .orElseThrow(() -> new NotFoundError("department not found"));
    }
}

package com.fawry.lms.department;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fawry.lms.shared.errors.NotFoundError;

@Service
public class DepartmentService {
    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public List<Department> getDepartments() {
        return departmentRepository.findAll();
    }

    public Department getDepartment(Long id) {
        return departmentRepository.findById(id)
            .orElseThrow(() -> new NotFoundError("department not found"));
    }

    public Department createDepartment(AddDepartmentRequest request) {
        Department department = new Department();
        department.setName(request.name);
        return departmentRepository.save(department);
    }

    public Department updateDepartment(Long id, AddDepartmentRequest request) {
        Department department = getDepartment(id);
        department.setName(request.name);
        return departmentRepository.save(department);
    }

    public void deleteDepartment(Long id) {
        departmentRepository.delete(getDepartment(id));
    }
}
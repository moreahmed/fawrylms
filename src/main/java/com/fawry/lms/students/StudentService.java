package com.fawry.lms.students;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fawry.lms.auth.Role;
import com.fawry.lms.auth.UserRepository;

@Service
public class StudentService {
    private final UserRepository userRepository;

    public StudentService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<StudentResponse> getStudents() {
        return userRepository.findByRole(Role.STUDENT).stream().map(StudentResponse::from).toList();
    }
}

package com.fawry.lms.instructors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping ("/instructors")
public class InstructorController {
    private final InstructorService instructorService;

    public InstructorController(InstructorService instructorService) {
        this.instructorService = instructorService;
    }

    @PreAuthorize ("hasRole('ADMIN')")
    @PostMapping
    public String createInstructor(@RequestBody AddInstructorRequest request) {
        instructorService.createInstructor(request);
        return "success";
    }
}

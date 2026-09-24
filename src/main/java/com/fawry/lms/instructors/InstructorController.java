package com.fawry.lms.instructors;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping ("/instructors")
public class InstructorController {
    private final InstructorService instructorService;

    public InstructorController(InstructorService instructorService) {
        this.instructorService = instructorService;
    }

    @GetMapping
    public List<Instructor> getInstructors() {
        return instructorService.getInstructors();
    }

    @GetMapping("/{id}")
    public Instructor getInstructor(@PathVariable Long id) {
        return instructorService.getInstructor(id);
    }

    @PreAuthorize ("hasRole('ADMIN')")
    @PostMapping
    public Instructor createInstructor(@RequestBody AddInstructorRequest request) {
        return instructorService.createInstructor(request);
    }

    @PreAuthorize ("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public Instructor updateInstructor(@PathVariable Long id, @RequestBody AddInstructorRequest request) {
        return instructorService.updateInstructor(id, request);
    }

    @PreAuthorize ("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteInstructor(@PathVariable Long id) {
        instructorService.deleteInstructor(id);
    }
}

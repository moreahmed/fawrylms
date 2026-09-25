package com.fawry.lms.semester;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/semester")
public class SemesterController {
    private final SemesterService semesterService;

    public SemesterController(SemesterService semesterService) { this.semesterService = semesterService; }

    @GetMapping
    public List<SemesterSettings> getSemesters() { return semesterService.getSemesters(); }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public SemesterSettings createSemester(@RequestBody SemesterSettingsRequest request) {
        return semesterService.createSemester(request);
    }

    @GetMapping("/active")
    public SemesterSettings getActiveSemester() {
        return semesterService.getActiveSemester();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public SemesterSettings updateSemester(@PathVariable Long id, @RequestBody SemesterSettingsRequest request) {
        return semesterService.updateSemester(id, request);
    }
}

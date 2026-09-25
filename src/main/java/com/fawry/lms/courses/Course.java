package com.fawry.lms.courses;

import java.time.LocalDate;
import java.util.List;

import com.fawry.lms.instructors.Instructor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity 
@Table (name = "courses")
public class Course {
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false)
    private String name;

    @Column (nullable = false)
    private String code;

    @Column (nullable = false, columnDefinition = "integer default 3")
    private int hours = 3;

    @Column
    private LocalDate issuedAt;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "prerequisite_id")
    private Course prerequisite;

    @ManyToMany (fetch = FetchType.LAZY)
    @JoinTable (name = "instructors_courses", joinColumns = @JoinColumn (name = "course_id"), inverseJoinColumns = @JoinColumn (name = "instructor_id"))
    private List<Instructor> instructors;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public LocalDate getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(LocalDate issuedAt) {
        this.issuedAt = issuedAt;
    }

    public Course getPrerequisite() {
        return prerequisite;
    }

    public void setPrerequisite(Course prerequisite) {
        this.prerequisite = prerequisite;
    }

    public List<Instructor> getInstructors() {
        return instructors;
    }

    public void setInstructors(List<Instructor> instructors) {
        this.instructors = instructors;
    }
}

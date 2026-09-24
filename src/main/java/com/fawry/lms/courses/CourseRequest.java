package com.fawry.lms.courses;

import java.time.LocalDate;
import java.util.List;

public class CourseRequest {
    public String name;
    public String code;
    public LocalDate issuedAt;
    public Long prerequisiteId;
    public List<Long> instructorIds;
}
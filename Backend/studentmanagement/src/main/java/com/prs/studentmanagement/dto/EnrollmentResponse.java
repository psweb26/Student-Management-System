package com.prs.studentmanagement.dto;

import java.sql.Date;

public class EnrollmentResponse {
    public Integer enrollmentId;
    public String studentId;
    public Integer courseId;
    public String courseCode;
    public String courseName;
    public Integer credits;
    public String grade;
    public Date enrollmentDate;

    public EnrollmentResponse() {}

    public EnrollmentResponse(Integer enrollmentId, String studentId, Integer courseId,
                              String courseCode, String courseName, Integer credits,
                              String grade, Date enrollmentDate) {
        this.enrollmentId = enrollmentId;
        this.studentId = studentId;
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.credits = credits;
        this.grade = grade;
        this.enrollmentDate = enrollmentDate;
    }
}
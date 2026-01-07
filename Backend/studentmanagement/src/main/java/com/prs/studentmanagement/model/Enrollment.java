package com.prs.studentmanagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "enrollments")
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer enrollmentId;

    // Foreign Key to Student (ManyToOne relationship) - FIXED and declared ONCE
    @ManyToOne(fetch = FetchType.LAZY)
    // IMPORTANT: Assuming the fix you intended for referencedColumnName="student_id"
    @JoinColumn(name = "student_id", referencedColumnName = "student_id", nullable = false)
    private Student student;

    // Foreign Key to Course (ManyToOne relationship) - ADDED BACK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "enrollment_date", nullable = false)
    private Date enrollmentDate;

    // Grade can be like 'A+', 'B', 'P', etc., so it's a string
    private String grade;
}
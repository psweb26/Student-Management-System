package com.prs.studentmanagement.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "students")
public class Student {

    @Id
    @Column(name = "student_id")
    private String id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String major;

    @Column(nullable = false)
    private int grade;

    @Column(nullable = false, unique = true)
    private String email;

    // Ensure password is accepted from requests but not serialized in responses:
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    private String password;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "date_of_birth")
    private java.sql.Date dateOfBirth;

    // NEW: optional profile fields used by the frontend
    @Column(name = "address")
    private String address;

    @Column(name = "program")
    private String program;

    // year can be null; integer for year-of-study
    @Column(name = "year")
    private Integer year;

    @Column(name = "advisor")
    private String advisor;
}
package com.prs.studentmanagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "fees")
// Prevent Jackson from trying to serialize hibernate proxy internals that often cause 500 errors.
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Fee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer feeId;

    // Foreign Key to Student (ManyToOne relationship)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", referencedColumnName = "student_id", nullable = false)
    @JsonIgnore // We will expose student id via a DTO; avoid serializing the full Student here.
    private Student student;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "due_date")
    private Date dueDate;

    // Status is an ENUM in your SQL, mapped to a String in Java for simplicity
    @Column(nullable = false)
    private String status; // Expects 'Paid', 'Pending', or 'Overdue'
}
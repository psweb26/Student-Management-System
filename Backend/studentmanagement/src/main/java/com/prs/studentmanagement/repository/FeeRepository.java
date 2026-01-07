package com.prs.studentmanagement.repository;

import com.prs.studentmanagement.model.Fee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeeRepository extends JpaRepository<Fee, Integer> {
    // Custom method to fetch all fees for a specific student, useful for the Parent/Student portal
    List<Fee> findByStudent_Id(String studentId);
}
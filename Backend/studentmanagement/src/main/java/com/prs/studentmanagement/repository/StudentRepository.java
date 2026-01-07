package com.prs.studentmanagement.repository;

import com.prs.studentmanagement.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {

    // Find student by email (useful for login)
    Optional<Student> findByEmail(String email);

    // Find student by email and password (for authentication)
    Optional<Student> findByEmailAndPassword(String email, String password);
}
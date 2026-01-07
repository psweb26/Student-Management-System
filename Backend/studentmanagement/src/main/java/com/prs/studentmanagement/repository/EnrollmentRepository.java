package com.prs.studentmanagement.repository;

import com.prs.studentmanagement.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {

    /**
     * Existing custom query: Finds a specific enrollment record by student ID and course code.
     */
    Optional<Enrollment> findByStudent_IdAndCourse_CourseCode(String studentId, String courseCode);

    /**
     * NEW: Finds all enrollment records for a given student ID.
     * * @param studentId The ID of the student.
     * @return A List of Enrollment objects for the specified student.
     */
    List<Enrollment> findByStudent_Id(String studentId);
}
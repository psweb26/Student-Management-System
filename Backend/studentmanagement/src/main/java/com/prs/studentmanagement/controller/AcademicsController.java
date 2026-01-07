package com.prs.studentmanagement.controller;

import com.prs.studentmanagement.dto.EnrollmentResponse;
import com.prs.studentmanagement.model.Enrollment;
import com.prs.studentmanagement.service.AcademicsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/academics")
public class AcademicsController {

    @Autowired
    private AcademicsService academicsService;

    public static class GradeUpdateDTO {
        public String studentId;
        public String courseCode;
        public String grade;
    }

    @PutMapping("/grade")
    public ResponseEntity<EnrollmentResponse> updateStudentGrade(@RequestBody GradeUpdateDTO updateDTO) {
        try {
            Enrollment updatedEnrollment = academicsService.updateGrade(
                    updateDTO.studentId,
                    updateDTO.courseCode,
                    updateDTO.grade
            );

            EnrollmentResponse resp = new EnrollmentResponse(
                    updatedEnrollment.getEnrollmentId(),
                    updatedEnrollment.getStudent() != null ? updatedEnrollment.getStudent().getId() : null,
                    updatedEnrollment.getCourse() != null ? updatedEnrollment.getCourse().getCourseId() : null,
                    updatedEnrollment.getCourse() != null ? updatedEnrollment.getCourse().getCourseCode() : null,
                    updatedEnrollment.getCourse() != null ? updatedEnrollment.getCourse().getCourseName() : null,
                    updatedEnrollment.getCourse() != null ? updatedEnrollment.getCourse().getCredits() : null,
                    updatedEnrollment.getGrade(),
                    updatedEnrollment.getEnrollmentDate()
            );

            return ResponseEntity.ok(resp);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
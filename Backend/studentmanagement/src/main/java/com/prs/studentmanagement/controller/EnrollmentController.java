package com.prs.studentmanagement.controller;

import com.prs.studentmanagement.dto.EnrollmentResponse;
import com.prs.studentmanagement.model.Enrollment;
import com.prs.studentmanagement.service.EnrollmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<EnrollmentResponse>> getEnrollmentsByStudent(@PathVariable String studentId) {
        List<Enrollment> enrollments = enrollmentService.getEnrollmentsByStudentId(studentId);
        List<EnrollmentResponse> response = enrollments.stream().map(e ->
                new EnrollmentResponse(
                        e.getEnrollmentId(),
                        e.getStudent() != null ? e.getStudent().getId() : null,
                        e.getCourse() != null ? e.getCourse().getCourseId() : null,
                        e.getCourse() != null ? e.getCourse().getCourseCode() : null,
                        e.getCourse() != null ? e.getCourse().getCourseName() : null,
                        e.getCourse() != null ? e.getCourse().getCredits() : null,
                        e.getGrade(),
                        e.getEnrollmentDate()
                )
        ).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}
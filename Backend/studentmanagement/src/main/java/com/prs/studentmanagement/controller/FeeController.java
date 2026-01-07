package com.prs.studentmanagement.controller;

import com.prs.studentmanagement.model.Fee;
import com.prs.studentmanagement.service.FeeService;
import com.prs.studentmanagement.dto.FeeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/fees")
public class FeeController {

    @Autowired
    private FeeService feeService;

    /**
     * Handles POST request to create a new fee invoice/record (used by Admin Panel).
     */
    @PostMapping
    public ResponseEntity<Fee> createFee(@RequestBody Fee fee) {
        return new ResponseEntity<>(feeService.createFee(fee), HttpStatus.CREATED);
    }

    /**
     * Handles GET request to view all fee records for a specific student.
     * Always returns 200 with an array (empty if none). This is more frontend-friendly.
     * Example: GET /api/v1/fees/student/RA24110441
     */
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<FeeResponse>> getFeesByStudentId(@PathVariable String studentId) {
        List<Fee> fees = feeService.getFeesByStudentId(studentId);
        // Map to DTOs to avoid serialization/LazyInitialization issues
        List<FeeResponse> response = fees.stream()
                .map(FeeResponse::new)
                .collect(Collectors.toList());
        // Return 200 + [] if no records — the frontend can decide how to display that.
        return ResponseEntity.ok(response);
    }

    /**
     * Handles PUT request to mark a fee as 'Paid' (simulating payment recording).
     * Example: PUT /api/v1/fees/101/pay
     */
    @PutMapping("/{feeId}/pay")
    public ResponseEntity<Fee> recordPayment(@PathVariable Integer feeId) {
        try {
            Fee paidFee = feeService.recordPayment(feeId);
            return ResponseEntity.ok(paidFee);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
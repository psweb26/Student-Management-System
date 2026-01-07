package com.prs.studentmanagement.dto;

import com.prs.studentmanagement.model.Fee;

import java.math.BigDecimal;
import java.sql.Date;

/**
 * Lightweight DTO for sending fee data to the frontend.
 * Placed in com.prs.studentmanagement.dto
 */
public class FeeResponse {

    public Integer feeId;
    public String studentId;
    public BigDecimal amount;
    public Date dueDate;
    public String status;

    public FeeResponse() {}

    public FeeResponse(Fee fee) {
        this.feeId = fee.getFeeId();
        // Assumes Student model exposes getId(); adjust to getStudentId() if your Student class uses that name.
        if (fee.getStudent() != null) {
            try {
                this.studentId = fee.getStudent().getId();
            } catch (NoSuchMethodError | Exception ex) {
                // Fallback if getter name differs
                try {
                    this.studentId = (String) fee.getStudent().getClass().getMethod("getStudentId").invoke(fee.getStudent());
                } catch (Exception e) {
                    this.studentId = null;
                }
            }
        } else {
            this.studentId = null;
        }
        this.amount = fee.getAmount();
        this.dueDate = fee.getDueDate();
        this.status = fee.getStatus();
    }
}
package com.prs.studentmanagement.dto;

/**
 * Lightweight DTO returned to the frontend for a parent's child.
 */
public class StudentSummary {
    public String id;
    public String firstName;
    public String lastName;
    public String email;

    public StudentSummary() {}

    public StudentSummary(String id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
}
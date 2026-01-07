package com.prs.studentmanagement.controller;

import com.prs.studentmanagement.dto.StudentSummary;
import com.prs.studentmanagement.model.Student;
import com.prs.studentmanagement.service.ParentService;
import com.prs.studentmanagement.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private ParentService parentService;

    public static class LoginRequest {
        public String username;
        public String password;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpSession session) {
        Student student = studentService.authenticate(loginRequest.username, loginRequest.password);

        if (student != null) {
            // Determine role
            String role;
            if (student.getId() != null && student.getId().startsWith("ADM")) {
                role = "admin";
            } else if (student.getId() != null && student.getId().startsWith("P")) {
                role = "parent";
            } else {
                role = "student";
            }

            // Store minimal session info (useful if the frontend uses cookies)
            session.setAttribute("currentUserId", student.getId());
            session.setAttribute("currentUserRole", role);

            // Build response
            AuthResponse resp = new AuthResponse(student, role);

            // If parent, include children in the response so frontend doesn't need an extra call
            if ("parent".equals(role)) {
                List<StudentSummary> children = parentService.getChildrenForParent(student.getId());
                resp.children = children;
            }

            return ResponseEntity.ok(resp);
        } else {
            return ResponseEntity.status(401).body(new ErrorResponse("Invalid username or password."));
        }
    }

    // FIXED AuthResponse - No password exposure!
    public static class AuthResponse {
        public String status = "success";
        public String id;
        public String firstName;
        public String email;
        public String role;

        // Optional: included for parents
        public List<StudentSummary> children;

        public AuthResponse() {}

        public AuthResponse(Student student, String role) {
            this.id = student.getId();
            this.firstName = student.getFirstName();
            this.email = student.getEmail();
            this.role = role;
        }
    }

    public static class ErrorResponse {
        public String message;
        public ErrorResponse(String message) {
            this.message = message;
        }
    }
}
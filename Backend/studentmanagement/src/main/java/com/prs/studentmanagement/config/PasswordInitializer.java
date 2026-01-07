package com.prs.studentmanagement.config;

import com.prs.studentmanagement.model.Student;
import com.prs.studentmanagement.repository.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

/**
 * Dev convenience: On startup, find any Student records whose password appears to be plaintext
 * (not starting with the bcrypt prefix) and replace them with bcrypt-hashed values.
 *
 * IMPORTANT:
 * - This is intended for development only (so your seeded data.sql with plaintext passwords like "1234"
 *   continue to work on first run).
 * - After the initial migration run, remove or disable this initializer and update data.sql to include
 *   hashed passwords, or keep it but be aware it will re-encode any non-bcrypt strings.
 */
@Component
public class PasswordInitializer implements CommandLineRunner {

    private final StudentRepository studentRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public PasswordInitializer(StudentRepository studentRepository, BCryptPasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        List<Student> students = studentRepository.findAll();
        int updated = 0;
        for (Student s : students) {
            String pw = s.getPassword();
            if (pw == null || pw.isEmpty()) continue;
            // Basic check for bcrypt prefix ($2a$, $2b$, $2y$). If missing, treat as plaintext and hash.
            if (!pw.startsWith("$2a$") && !pw.startsWith("$2b$") && !pw.startsWith("$2y$")) {
                String hashed = passwordEncoder.encode(pw);
                s.setPassword(hashed);
                studentRepository.save(s);
                updated++;
            }
        }
        if (updated > 0) {
            System.out.println("PasswordInitializer: hashed " + updated + " plaintext passwords (dev migration).");
        } else {
            System.out.println("PasswordInitializer: no plaintext passwords found.");
        }
    }
}
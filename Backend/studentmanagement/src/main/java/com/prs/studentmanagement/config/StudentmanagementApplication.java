package com.prs.studentmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.prs.studentmanagement",         // main application package
        "com.prs.studentmanagement.controller",
        "com.prs.studentmanagement.service",
        "com.prs.studentmanagement.repository"
})
public class StudentmanagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudentmanagementApplication.class, args);
    }

}

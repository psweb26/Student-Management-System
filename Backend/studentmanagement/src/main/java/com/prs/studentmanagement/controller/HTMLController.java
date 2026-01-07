package com.prs.studentmanagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HTMLController {
    @GetMapping("/")
    public String index() {
        return "index.html";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard.html";
    }

    @GetMapping("/parent")
    public String parent() {
        return "parent.html";
    }

    @GetMapping("/profile")
    public String profile() {
        return "profile.html";
    }

    @GetMapping("/reports")
    public String reports() {
        return "reports.html";
    }

    @GetMapping("/test")
    public String test() {
        return "test.html";
    }
}

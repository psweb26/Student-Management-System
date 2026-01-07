package com.prs.studentmanagement.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Configure this to your frontend origin for dev, e.g. http://localhost:63342 or http://localhost:3000
    @Value("${app.cors.allowed-origins:http://localhost:63342}")
    private String allowedOrigin;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        System.out.println("✅ CORS Configuration Applied for " + allowedOrigin);
        registry.addMapping("/api/v1/**")
                .allowedOrigins(allowedOrigin)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)  // required for session cookies to be accepted by browser
                .maxAge(3600);
    }
}
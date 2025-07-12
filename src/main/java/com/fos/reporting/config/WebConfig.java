package com.fos.reporting.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/") // Apply to all paths
                .allowedOrigins("https://pulse-fe-293050141084.asia-south1.run.app", "http://localhost:5173") // Specific allowed origins
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allowed HTTP methods
                .allowedHeaders("*") // Allowed headers
                .allowCredentials(true) // Allow sending credentials (e.g., cookies)
                .maxAge(3600); // Preflight request cache duration
        }
    }


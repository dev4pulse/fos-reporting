package com.fos.reporting.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Match all endpoints
                .allowedOrigins("*") // Allow all origins (or specify your frontend URL)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Include OPTIONS!
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}

package com.app.gizmophile.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Apply CORS to all paths
//                .allowedOrigins("http://gizmophileapp.s3-website.ap-south-1.amazonaws.com/") // Allow all origins
                .allowedOrigins("http://localhost:4200/") // Allow all origins
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS") // Specify allowed methods
                .allowedHeaders("*") // Allow all headers
                .allowCredentials(true) // This allows sending cookies and other credentials. Set to false if not needed.
                .maxAge(3600); // Cache the CORS configuration for 1 hour
    }
}

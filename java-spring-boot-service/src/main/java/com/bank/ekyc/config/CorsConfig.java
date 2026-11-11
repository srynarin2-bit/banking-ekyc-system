package com.bank.ekyc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // អនុញ្ញាតគ្រប់ Endpoint ទាំងអស់
                        .allowedOriginPatterns("*") // អនុញ្ញាតគ្រប់ Origin ទាំងអស់ (សម្រាប់ Mobile App & Web)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // អនុញ្ញាតគ្រប់ HTTP Methods
                        .allowedHeaders("*") // អនុញ្ញាតគ្រប់ Headers ទាំងអស់
                        .allowCredentials(true);
            }
        };
    }
}
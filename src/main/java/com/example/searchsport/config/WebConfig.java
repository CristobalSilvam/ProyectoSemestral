package com.example.searchsport.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Le dice a Spring que cualquier petición a /uploads/ busque en la carpeta física "uploads"
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}
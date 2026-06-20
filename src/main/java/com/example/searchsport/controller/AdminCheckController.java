package com.example.searchsport.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminCheckController {

    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkAdmin(Authentication authentication) {
        return ResponseEntity.ok(Map.of(
                "ok", true,
                "usuario", authentication.getName(),
                "authorities", authentication.getAuthorities()
        ));
    }
}
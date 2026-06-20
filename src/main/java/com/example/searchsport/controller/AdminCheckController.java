package com.example.searchsport.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminCheckController {

    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkAdmin() {
        return ResponseEntity.ok(Map.of(
                "ok", true,
                "rol", "ADMIN"
        ));
    }
}
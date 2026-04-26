package com.example.searchsport.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.searchsport.dto.ReputacionDTO;
import com.example.searchsport.dto.ReviewRequest;
import com.example.searchsport.service.ReviewService;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    private String getEmailAutenticado() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    // HU-14: Deportista deja una reseña
    @PostMapping
    public ResponseEntity<?> publicarReview(@RequestBody ReviewRequest request) {
        try {
            return ResponseEntity.ok(reviewService.dejarReview(request, getEmailAutenticado()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // HU-15: Consultar la reputación de un recinto
    @GetMapping("/recinto/{recintoId}/reputacion")
    public ResponseEntity<ReputacionDTO> verReputacion(@PathVariable Long recintoId) {
        return ResponseEntity.ok(reviewService.obtenerReputacionRecinto(recintoId));
    }
}
package com.example.searchsport.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "REVIEW")
@Data
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_review")
    private Long idReview;

    @Column(nullable = false)
    private Byte puntaje; // Entre 1 y 5

    @Column(columnDefinition = "TEXT")
    private String comentario;
}
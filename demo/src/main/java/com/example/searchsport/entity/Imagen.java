package com.example.searchsport.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "IMAGEN")
@Data
public class Imagen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_img")
    private Long idImg;

    @Column(nullable = false, length = 255)
    private String url;

    @ManyToOne
    @JoinColumn(name = "recinto_id", nullable = false)
    private Recinto recinto;
}
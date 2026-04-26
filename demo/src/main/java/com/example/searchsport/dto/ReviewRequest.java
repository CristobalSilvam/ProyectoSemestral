package com.example.searchsport.dto;

import lombok.Data;

@Data
public class ReviewRequest {
    private Long reservaId;
    private Byte puntaje; // De 1 a 5 estrellas
    private String comentario;
}
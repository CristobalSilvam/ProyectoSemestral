package com.example.searchsport.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReputacionDTO {
    private Long recintoId;
    private Double promedioEstrellas;
    private Long totalResenas;
}
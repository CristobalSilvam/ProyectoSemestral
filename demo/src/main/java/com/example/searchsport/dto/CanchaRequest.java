package com.example.searchsport.dto;

import lombok.Data;

@Data
public class CanchaRequest {
    private String nombreInterno;
    private Boolean esTechada;
    private String tipoSuperficie;
    private Long recintoId;
    private Long deporteId;
}
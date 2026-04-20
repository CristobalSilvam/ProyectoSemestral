package com.example.searchsport.dto;

import lombok.Data;

@Data
public class RecintoRequest {
    // Datos del Recinto
    private String nombre;
    private String rutEmpresa;
    
    // Datos de la Dirección
    private String calle;
    private Integer numero;
    private String region;
    private Long comunaId;
}
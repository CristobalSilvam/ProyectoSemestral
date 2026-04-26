package com.example.searchsport.dto;

import java.math.BigDecimal;

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
    private String comuna;
    // Datos para las coordenadas
    private BigDecimal latitud;
    private BigDecimal longitud;
}
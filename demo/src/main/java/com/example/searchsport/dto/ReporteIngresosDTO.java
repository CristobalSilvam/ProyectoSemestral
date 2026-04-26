package com.example.searchsport.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ReporteIngresosDTO {
    private Long recintoId;
    private int mes;
    private int anio;
    private Long totalReservasPagadas;
    private BigDecimal ingresosTotales;
}
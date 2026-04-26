package com.example.searchsport.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalTime;

@Data
public class TarifaRequest {
    private Long canchaId;
    private Byte diaSemana; // 1 (Lunes) a 7 (Domingo) 
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private BigDecimal precio;
}
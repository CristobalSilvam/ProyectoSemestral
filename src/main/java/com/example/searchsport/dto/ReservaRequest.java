package com.example.searchsport.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ReservaRequest {
    private Long canchaId;
    private LocalDate fechaUso;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private BigDecimal montoTotal;
}
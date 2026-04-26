package com.example.searchsport.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalTime;

@Data
@AllArgsConstructor
public class BloqueDisponibleDTO {
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private BigDecimal precio;
}
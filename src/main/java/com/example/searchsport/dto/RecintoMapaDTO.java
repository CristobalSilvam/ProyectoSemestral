package com.example.searchsport.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class RecintoMapaDTO {
    private Long idRecinto;
    private String nombre;
    private BigDecimal latitud;
    private BigDecimal longitud;
    private BigDecimal precioDesde; // Mostrará el precio más barato de ese recinto
}
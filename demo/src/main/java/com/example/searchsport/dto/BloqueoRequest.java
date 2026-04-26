package com.example.searchsport.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class BloqueoRequest {
    private Long canchaId;
    private LocalDate fecha;
    private String motivo; 
}
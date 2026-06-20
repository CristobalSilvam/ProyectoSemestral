package com.example.searchsport.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminRecintoDTO {

    private Long id;

    private String nombre;

    private String rutEmpresa;

    private Boolean aprobado;

    private String nombreDueno;

    private String emailDueno;

    private String calle;

    private Integer numero;

    private String comuna;

    private String region;

    private Integer cantidadCanchas;
}
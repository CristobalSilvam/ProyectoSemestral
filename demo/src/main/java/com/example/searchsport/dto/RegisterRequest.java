package com.example.searchsport.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String rut;
    private String nombre;
    private String segundoNombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String email;
    private String password;
}
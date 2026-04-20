package com.example.searchsport.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Atrapa errores de la base de datos, como los UNIQUE de RUT o Email
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        String mensaje = "Error de integridad de datos.";

        // Buscamos si el error menciona el RUT o el Email para dar un mensaje específico
        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("rut")) {
                mensaje = "Este RUT ya se encuentra registrado.";
            } else if (ex.getMessage().contains("email")) {
                mensaje = "Este correo electrónico ya se encuentra registrado.";
            }
        }

        errorResponse.put("error", mensaje);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Un atrapador general por si se nos escapa algún otro error 
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Ocurrió un error interno en el servidor.");
        errorResponse.put("detalle", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
package com.example.searchsport.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleDataIntegrityViolation_debeRetornarMensajeRutDuplicado() {
        DataIntegrityViolationException ex =
                new DataIntegrityViolationException("Duplicate entry for rut");

        ResponseEntity<Map<String, String>> response =
                handler.handleDataIntegrityViolation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Este RUT ya se encuentra registrado.", response.getBody().get("error"));
    }

    @Test
    void handleDataIntegrityViolation_debeRetornarMensajeEmailDuplicado() {
        DataIntegrityViolationException ex =
                new DataIntegrityViolationException("Duplicate entry for email");

        ResponseEntity<Map<String, String>> response =
                handler.handleDataIntegrityViolation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Este correo electrónico ya se encuentra registrado.", response.getBody().get("error"));
    }

    @Test
    void handleDataIntegrityViolation_debeRetornarMensajeGenericoSiNoEsRutNiEmail() {
        DataIntegrityViolationException ex =
                new DataIntegrityViolationException("Foreign key constraint fails");

        ResponseEntity<Map<String, String>> response =
                handler.handleDataIntegrityViolation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Error de integridad de datos.", response.getBody().get("error"));
    }

    @Test
    void handleDataIntegrityViolation_debeRetornarMensajeGenericoSiMensajeEsNull() {
        DataIntegrityViolationException ex =
                new DataIntegrityViolationException(null);

        ResponseEntity<Map<String, String>> response =
                handler.handleDataIntegrityViolation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Error de integridad de datos.", response.getBody().get("error"));
    }

    @Test
    void handleGeneralException_debeRetornarErrorInternoConDetalle() {
        Exception ex = new Exception("Error inesperado");

        ResponseEntity<Map<String, String>> response =
                handler.handleGeneralException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Ocurrió un error interno en el servidor.", response.getBody().get("error"));
        assertEquals("Error inesperado", response.getBody().get("detalle"));
    }
}
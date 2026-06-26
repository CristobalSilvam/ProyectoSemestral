package com.example.searchsport.controller;

import com.example.searchsport.dto.ReservaRequest;
import com.example.searchsport.entity.Reserva;
import com.example.searchsport.service.ReservaService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservaControllerTest {

    @AfterEach
    void limpiarContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void verHistorial_debeRetornarReservasSiUsuarioEstaAutenticado() {
        ReservaService reservaService = mock(ReservaService.class);
        ReservaController controller = crearController(reservaService);

        autenticar("cliente@email.com");

        Reserva reserva = new Reserva();
        reserva.setIdReserva(1L);

        when(reservaService.obtenerHistorial("cliente@email.com")).thenReturn(List.of(reserva));

        ResponseEntity<?> response = controller.verHistorial();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(reserva), response.getBody());
        verify(reservaService).obtenerHistorial("cliente@email.com");
    }

    @Test
    void verHistorial_debeRetornarUnauthorizedSiNoHayAutenticacion() {
        ReservaController controller = crearController(mock(ReservaService.class));

        ResponseEntity<?> response = controller.verHistorial();

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Debes iniciar sesión para ver tus reservas", obtenerMensaje(response));
    }

    @Test
    void verHistorial_debeRetornarUnauthorizedSiUsuarioNoExiste() {
        ReservaService reservaService = mock(ReservaService.class);
        ReservaController controller = crearController(reservaService);

        autenticar("cliente@email.com");

        when(reservaService.obtenerHistorial("cliente@email.com"))
                .thenThrow(new RuntimeException("Usuario no encontrado"));

        ResponseEntity<?> response = controller.verHistorial();

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("La sesión no es válida. Inicia sesión nuevamente.", obtenerMensaje(response));
    }

    @Test
    void procesarPago_debeRetornarReservaPagadaSiUsuarioEstaAutenticado() {
        ReservaService reservaService = mock(ReservaService.class);
        ReservaController controller = crearController(reservaService);

        autenticar("cliente@email.com");

        Reserva reserva = new Reserva();
        reserva.setIdReserva(1L);

        when(reservaService.confirmarPago(1L, "cliente@email.com")).thenReturn(reserva);

        ResponseEntity<?> response = controller.procesarPago(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(reserva, response.getBody());
        verify(reservaService).confirmarPago(1L, "cliente@email.com");
    }

    @Test
    void procesarPago_debeRetornarUnauthorizedSiUsuarioNoEstaAutenticado() {
        ReservaController controller = crearController(mock(ReservaService.class));

        ResponseEntity<?> response = controller.procesarPago(1L);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Debes iniciar sesión para pagar una reserva", obtenerMensaje(response));
    }

    @Test
    void procesarPago_debeRetornarBadRequestSiServicioLanzaError() {
        ReservaService reservaService = mock(ReservaService.class);
        ReservaController controller = crearController(reservaService);

        autenticar("cliente@email.com");

        when(reservaService.confirmarPago(1L, "cliente@email.com"))
                .thenThrow(new RuntimeException("Reserva no encontrada"));

        ResponseEntity<?> response = controller.procesarPago(1L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Reserva no encontrada", obtenerMensaje(response));
    }

    @Test
    void cancelar_debeRetornarReservaCanceladaSiUsuarioEstaAutenticado() {
        ReservaService reservaService = mock(ReservaService.class);
        ReservaController controller = crearController(reservaService);

        autenticar("cliente@email.com");

        Reserva reserva = new Reserva();
        reserva.setIdReserva(1L);

        when(reservaService.cancelarReserva(1L, "cliente@email.com")).thenReturn(reserva);

        ResponseEntity<?> response = controller.cancelar(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(reserva, response.getBody());
        verify(reservaService).cancelarReserva(1L, "cliente@email.com");
    }

    @Test
    void cancelar_debeRetornarUnauthorizedSiUsuarioNoEstaAutenticado() {
        ReservaController controller = crearController(mock(ReservaService.class));

        ResponseEntity<?> response = controller.cancelar(1L);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Debes iniciar sesión para cancelar una reserva", obtenerMensaje(response));
    }

    @Test
    void cancelar_debeRetornarBadRequestSiServicioLanzaError() {
        ReservaService reservaService = mock(ReservaService.class);
        ReservaController controller = crearController(reservaService);

        autenticar("cliente@email.com");

        when(reservaService.cancelarReserva(1L, "cliente@email.com"))
                .thenThrow(new RuntimeException("Solo puedes cancelar con al menos 24 horas de antelación"));

        ResponseEntity<?> response = controller.cancelar(1L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Solo puedes cancelar con al menos 24 horas de antelación", obtenerMensaje(response));
    }

    @Test
    void crearReserva_debeRetornarCreatedSiUsuarioEstaAutenticado() {
        ReservaService reservaService = mock(ReservaService.class);
        ReservaController controller = crearController(reservaService);

        autenticar("cliente@email.com");

        ReservaRequest request = new ReservaRequest();
        Reserva reserva = new Reserva();
        reserva.setIdReserva(1L);

        when(reservaService.crearReserva(request, "cliente@email.com")).thenReturn(reserva);

        ResponseEntity<?> response = controller.crearReserva(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(reserva, response.getBody());
        verify(reservaService).crearReserva(request, "cliente@email.com");
    }

    @Test
    void crearReserva_debeRetornarUnauthorizedSiUsuarioNoEstaAutenticado() {
        ReservaController controller = crearController(mock(ReservaService.class));

        ResponseEntity<?> response = controller.crearReserva(new ReservaRequest());

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Debes iniciar sesión para crear una reserva", obtenerMensaje(response));
    }

    @Test
    void crearReserva_debeRetornarUnauthorizedSiUsuarioNoExiste() {
        ReservaService reservaService = mock(ReservaService.class);
        ReservaController controller = crearController(reservaService);

        autenticar("cliente@email.com");

        ReservaRequest request = new ReservaRequest();

        when(reservaService.crearReserva(request, "cliente@email.com"))
                .thenThrow(new RuntimeException("Usuario no encontrado en la base de datos"));

        ResponseEntity<?> response = controller.crearReserva(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("La sesión no es válida. Inicia sesión nuevamente.", obtenerMensaje(response));
    }

    @Test
    void crearReserva_debeRetornarBadRequestSiServicioLanzaError() {
        ReservaService reservaService = mock(ReservaService.class);
        ReservaController controller = crearController(reservaService);

        autenticar("cliente@email.com");

        ReservaRequest request = new ReservaRequest();

        when(reservaService.crearReserva(request, "cliente@email.com"))
                .thenThrow(new RuntimeException("Lo sentimos, este bloque horario acaba de ser reservado por alguien más."));

        ResponseEntity<?> response = controller.crearReserva(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Lo sentimos, este bloque horario acaba de ser reservado por alguien más.", obtenerMensaje(response));
    }

    @Test
    void crearReserva_debeRetornarUnauthorizedSiAuthenticationEsAnonymousUser() {
        ReservaController controller = crearController(mock(ReservaService.class));

        autenticar("anonymousUser");

        ResponseEntity<?> response = controller.crearReserva(new ReservaRequest());

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Debes iniciar sesión para crear una reserva", obtenerMensaje(response));
    }

    private ReservaController crearController(ReservaService reservaService) {
        ReservaController controller = new ReservaController();

        try {
            java.lang.reflect.Field field = ReservaController.class.getDeclaredField("reservaService");
            field.setAccessible(true);
            field.set(controller, reservaService);
            return controller;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void autenticar(String email) {
        TestingAuthenticationToken authentication = new TestingAuthenticationToken(email, null);
        authentication.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @SuppressWarnings("unchecked")
    private String obtenerMensaje(ResponseEntity<?> response) {
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertNotNull(body);
        return body.get("message");
    }
}
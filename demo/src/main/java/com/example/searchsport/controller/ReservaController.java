package com.example.searchsport.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.searchsport.dto.ReservaRequest;
import com.example.searchsport.entity.Reserva;
import com.example.searchsport.service.ReservaService;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    private String getEmailAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Usuario no autenticado");
        }

        String email = authentication.getName();

        if (email == null || email.isBlank() || email.equals("anonymousUser")) {
            throw new RuntimeException("Usuario no autenticado");
        }

        return email;
    }

    // GET /api/reservas/mis-reservas
    @GetMapping("/mis-reservas")
    public ResponseEntity<?> verHistorial() {
        try {
            String emailUsuario = getEmailAutenticado();
            List<Reserva> reservas = reservaService.obtenerHistorial(emailUsuario);
            return ResponseEntity.ok(reservas);

        } catch (RuntimeException e) {
            String mensaje = e.getMessage();

            if (mensaje != null && mensaje.toLowerCase().contains("no autenticado")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Debes iniciar sesión para ver tus reservas"));
            }

            if (mensaje != null && mensaje.toLowerCase().contains("usuario no encontrado")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "La sesión no es válida. Inicia sesión nuevamente."));
            }

            return ResponseEntity.badRequest()
                    .body(Map.of("message", mensaje != null ? mensaje : "No se pudieron cargar las reservas"));
        }
    }

    // POST /api/reservas/{id}/pago
    @PostMapping("/{id}/pago")
    public ResponseEntity<?> procesarPago(@PathVariable Long id) {
        try {
            String emailUsuario = getEmailAutenticado();
            Reserva reserva = reservaService.confirmarPago(id, emailUsuario);
            return ResponseEntity.ok(reserva);

        } catch (RuntimeException e) {
            String mensaje = e.getMessage();

            if (mensaje != null && mensaje.toLowerCase().contains("no autenticado")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Debes iniciar sesión para pagar una reserva"));
            }

            return ResponseEntity.badRequest()
                    .body(Map.of("message", mensaje != null ? mensaje : "No se pudo confirmar el pago"));
        }
    }

    // PATCH /api/reservas/{id}/cancelar
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelar(@PathVariable Long id) {
        try {
            String emailUsuario = getEmailAutenticado();
            Reserva reserva = reservaService.cancelarReserva(id, emailUsuario);
            return ResponseEntity.ok(reserva);

        } catch (RuntimeException e) {
            String mensaje = e.getMessage();

            if (mensaje != null && mensaje.toLowerCase().contains("no autenticado")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Debes iniciar sesión para cancelar una reserva"));
            }

            return ResponseEntity.badRequest()
                    .body(Map.of("message", mensaje != null ? mensaje : "No se pudo cancelar la reserva"));
        }
    }

    // POST /api/reservas
    @PostMapping
    public ResponseEntity<?> crearReserva(@RequestBody ReservaRequest request) {
        try {
            String emailUsuario = getEmailAutenticado();
            Reserva reservaCreada = reservaService.crearReserva(request, emailUsuario);
            return new ResponseEntity<>(reservaCreada, HttpStatus.CREATED);

        } catch (RuntimeException e) {
            String mensaje = e.getMessage();

            if (mensaje != null && mensaje.toLowerCase().contains("no autenticado")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Debes iniciar sesión para crear una reserva"));
            }

            if (mensaje != null && mensaje.toLowerCase().contains("usuario no encontrado")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "La sesión no es válida. Inicia sesión nuevamente."));
            }

            return ResponseEntity.badRequest()
                    .body(Map.of("message", mensaje != null ? mensaje : "No se pudo crear la reserva"));
        }
    }
}
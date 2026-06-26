package com.example.searchsport.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody; // <-- Importante: Agregamos tu PagoService
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.searchsport.dto.ReservaRequest;
import com.example.searchsport.entity.Reserva;
import com.example.searchsport.service.PagoService;
import com.example.searchsport.service.ReservaService;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private PagoService pagoService; // <-- Inyectamos Mercado Pago

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

    // -------------------------------------------------------------------
    // PASO 1 PAGO: Generar la ventana de Mercado Pago
    // -------------------------------------------------------------------
    @PostMapping("/{id}/pago")
    public ResponseEntity<?> generarPreferenciaPago(@PathVariable Long id) {
        try {
            String emailUsuario = getEmailAutenticado();

            // Aquí usaré datos de prueba. Más adelante puedes hacer:
            // Reserva reserva = reservaService.obtenerPorId(id);
            // BigDecimal monto = reserva.getMontoTotal();
            String tituloPrueba = "Reserva Cancha (Test ID: " + id + ")";
            BigDecimal montoPrueba = new BigDecimal("15000"); // 15.000 pesos

            // Llamamos a Mercado Pago
            String preferenceId = pagoService.crearPreferenciaPago(tituloPrueba, montoPrueba);

            return ResponseEntity.ok(Map.of(
                "preferenceId", preferenceId,
                "status", "success"
            ));

        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("no autenticado")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Debes iniciar sesión para pagar una reserva"));
            }
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("message", "Error al conectar con Mercado Pago: " + e.getMessage()));
        }
    }

    // -------------------------------------------------------------------
    // PASO 2 PAGO: Tu lógica original (Confirma en la BD que ya se pagó)
    // -------------------------------------------------------------------
    @PostMapping("/{id}/confirmar")
    public ResponseEntity<?> procesarPagoExitoso(@PathVariable Long id) {
        try {
            String emailUsuario = getEmailAutenticado();
            // Ejecuta tu lógica original de cambiar el estado a "PAGADO"
            Reserva reserva = reservaService.confirmarPago(id, emailUsuario);
            return ResponseEntity.ok(reserva);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
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
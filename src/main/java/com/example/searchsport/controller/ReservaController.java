package com.example.searchsport.controller;

import java.util.List;

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

    // Obtener email del usuario autenticado vía JWT
    private String getEmailAutenticado() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
    // GET /api/reservas/mis-reservas (HU-10)
    @GetMapping("/mis-reservas")
    public ResponseEntity<List<Reserva>> verHistorial() {
        return ResponseEntity.ok(reservaService.obtenerHistorial(getEmailAutenticado()));
    }

    // POST /api/reservas/{id}/pago (HU-08)
    @PostMapping("/{id}/pago")
    public ResponseEntity<?> procesarPago(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(reservaService.confirmarPago(id, getEmailAutenticado()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // PATCH /api/reservas/{id}/cancelar (HU-09)
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(reservaService.cancelarReserva(id, getEmailAutenticado()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    // Endpoint: POST /api/reservas
    @PostMapping
    public ResponseEntity<?> crearReserva(@RequestBody ReservaRequest request) {
        try {
            // Extraemos la información del usuario autenticado desde el Token JWT
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String emailUsuarioLogueado = authentication.getName(); // Devuelve el "subject" del token (el email)

            // Procesamos la reserva
            Reserva reservaCreada = reservaService.crearReserva(request, emailUsuarioLogueado);
            
            return new ResponseEntity<>(reservaCreada, HttpStatus.CREATED);

        } catch (RuntimeException ex) {
            // Si salta nuestra alerta de Overbooking, devolvemos un 400 Bad Request
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
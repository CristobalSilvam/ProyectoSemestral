package com.example.searchsport.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.searchsport.dto.ReservaRequest;
import com.example.searchsport.entity.Reserva;
import com.example.searchsport.service.ReservaService;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @PostMapping
    public ResponseEntity<?> crearReserva(
            @RequestBody ReservaRequest request,
            Authentication authentication
    ) {
        String emailUsuario = obtenerEmailUsuario(authentication);

        Reserva reserva = reservaService.crearReserva(request, emailUsuario);

        return ResponseEntity.ok(reserva);
    }

    @PostMapping("/{id}/pago")
    public ResponseEntity<?> confirmarPago(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String emailUsuario = obtenerEmailUsuario(authentication);

        Reserva reserva = reservaService.confirmarPago(id, emailUsuario);

        return ResponseEntity.ok(reserva);
    }

    @RequestMapping(
            value = "/{id}/cancelar",
            method = {RequestMethod.POST, RequestMethod.PATCH}
    )
    public ResponseEntity<?> cancelarReserva(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String emailUsuario = obtenerEmailUsuario(authentication);

        Reserva reserva = reservaService.cancelarReserva(id, emailUsuario);

        return ResponseEntity.ok(reserva);
    }

    @GetMapping("/mis-reservas")
    public ResponseEntity<?> obtenerMisReservas(Authentication authentication) {
        String emailUsuario = obtenerEmailUsuario(authentication);

        List<Reserva> reservas = reservaService.obtenerHistorial(emailUsuario);

        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/historial")
    public ResponseEntity<?> obtenerHistorial(Authentication authentication) {
        String emailUsuario = obtenerEmailUsuario(authentication);

        List<Reserva> reservas = reservaService.obtenerHistorial(emailUsuario);

        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerReservaPorId(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String emailUsuario = obtenerEmailUsuario(authentication);

        Reserva reserva = reservaService.obtenerReservaPorId(id, emailUsuario);

        return ResponseEntity.ok(reserva);
    }

    private String obtenerEmailUsuario(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("Usuario no autenticado");
        }

        return authentication.getName();
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> manejarRuntimeException(RuntimeException e) {
        return ResponseEntity.badRequest().body(Map.of(
                "message", e.getMessage()
        ));
    }
}
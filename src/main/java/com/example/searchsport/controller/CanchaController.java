package com.example.searchsport.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.searchsport.dto.BloqueDisponibleDTO;
import com.example.searchsport.service.DisponibilidadService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/canchas")
public class CanchaController {

    @Autowired
    private DisponibilidadService disponibilidadService;

    // Endpoint: GET /api/canchas/{id}/disponibilidad?fecha=YYYY-MM-DD
    @GetMapping("/{id}/disponibilidad")
    public ResponseEntity<List<BloqueDisponibleDTO>> consultarDisponibilidad(
            @PathVariable("id") Long canchaId,
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        
        List<BloqueDisponibleDTO> disponibles = disponibilidadService.obtenerBloquesDisponibles(canchaId, fecha);
        return ResponseEntity.ok(disponibles);
    }
}
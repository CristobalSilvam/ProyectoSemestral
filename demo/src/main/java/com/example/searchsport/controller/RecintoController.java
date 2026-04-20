package com.example.searchsport.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.searchsport.entity.Recinto;
import com.example.searchsport.service.RecintoService;

@RestController
@RequestMapping("/api/recintos")
public class RecintoController {

    @Autowired
    private RecintoService recintoService;

    // Endpoint: GET /api/recintos
    @GetMapping
    public ResponseEntity<List<Recinto>> listarTodos() {
        List<Recinto> recintos = recintoService.obtenerTodos();
        return ResponseEntity.ok(recintos);
    }

    // Endpoint: GET /api/recintos/search?deporte=&precioMax=
    // Aquí dejaremos el esqueleto listo para lo que me comentaste en tu documento
    @GetMapping("/search")
    public ResponseEntity<?> buscarRecintos(
            @RequestParam(required = false) String deporte,
            @RequestParam(required = false) Double precioMax) {
        
        // TODO: Implementar la lógica de búsqueda con coordenadas, filtros, etc.
        return ResponseEntity.ok("Búsqueda en construcción. Deporte: " + deporte + ", Precio Max: " + precioMax);
    }
}
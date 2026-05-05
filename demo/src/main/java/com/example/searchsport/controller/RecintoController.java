package com.example.searchsport.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.searchsport.dto.RecintoMapaDTO;
import com.example.searchsport.entity.Recinto;
import com.example.searchsport.service.RecintoService;

@RestController
@RequestMapping("/api/recintos")
public class RecintoController {

    @Autowired
    private RecintoService recintoService;

    @GetMapping
    public ResponseEntity<List<Recinto>> listarTodos() {
        List<Recinto> recintos = recintoService.obtenerTodos();
        return ResponseEntity.ok(recintos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recinto> obtenerPorId(@PathVariable Long id) {
        Optional<Recinto> recinto = recintoService.obtenerPorId(id);

        return recinto.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<RecintoMapaDTO>> buscarRecintos(
            @RequestParam(required = false) String deporte,
            @RequestParam(required = false) BigDecimal precioMax) {

        List<RecintoMapaDTO> resultados = recintoService.buscarParaMapa(deporte, precioMax);
        return ResponseEntity.ok(resultados);
    }
}
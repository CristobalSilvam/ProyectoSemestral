package com.example.searchsport.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.searchsport.entity.Deporte;
import com.example.searchsport.repository.DeporteRepository;

@RestController
@RequestMapping("/api/deportes")
public class DeporteController {

    @Autowired
    private DeporteRepository deporteRepository;

    @GetMapping
    public ResponseEntity<List<Deporte>> listarDeportes() {
        try {
            List<Deporte> listaDeportes = deporteRepository.findAll();
            return ResponseEntity.ok(listaDeportes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
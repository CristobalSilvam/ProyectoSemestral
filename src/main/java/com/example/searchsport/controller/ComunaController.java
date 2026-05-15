package com.example.searchsport.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.searchsport.entity.Comuna;
import com.example.searchsport.repository.ComunaRepository;

@RestController
@RequestMapping("/api/comunas")
public class ComunaController {

    @Autowired
    private ComunaRepository comunaRepository;

    @GetMapping
    public List<Comuna> listarComunas() {
        return comunaRepository.findAll();
    }

    @GetMapping("/region/{regionId}")
    public List<Comuna> listarComunasPorRegion(@PathVariable Long regionId) {
        return comunaRepository.findByRegion_Id(regionId);
    }
}
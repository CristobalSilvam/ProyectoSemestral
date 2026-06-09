package com.example.searchsport.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.searchsport.entity.Region;
import com.example.searchsport.repository.RegionRepository; // Asegúrate de tener este repositorio creado

@RestController
@RequestMapping("/api/regiones")
public class RegionController {

    @Autowired
    private RegionRepository regionRepository;

    @GetMapping
    public List<Region> listarRegiones() {
        return regionRepository.findAll();
    }
}
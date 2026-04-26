package com.example.searchsport.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.searchsport.dto.RecintoMapaDTO;
import com.example.searchsport.dto.RecintoRequest;
import com.example.searchsport.entity.Comuna;
import com.example.searchsport.entity.Coordenada;
import com.example.searchsport.entity.Direccion;
import com.example.searchsport.entity.Recinto;
import com.example.searchsport.repository.DireccionRepository;
import com.example.searchsport.repository.RecintoRepository;
import com.example.searchsport.repository.RegionRepository;
import com.example.searchsport.repository.ComunaRepository;
import com.example.searchsport.repository.CoordenadaRepository;
import com.example.searchsport.entity.Region;

@Service
public class RecintoService {

    public List<RecintoMapaDTO> buscarParaMapa(String deporte, BigDecimal precioMax) {
        return recintoRepository.buscarRecintosParaMapa(deporte, precioMax);
    }

    @Autowired
    private RecintoRepository recintoRepository;

    @Autowired
    private DireccionRepository direccionRepository;

    @Autowired
    private CoordenadaRepository coordenadaRepository;

    @Autowired
    private ComunaRepository comunaRepository;
    
    @Autowired
    private RegionRepository regionRepository;


    public java.util.List<Recinto> obtenerTodos() {
        return recintoRepository.findAll();
    }

    @Transactional
    public Recinto crearRecinto(RecintoRequest request) {
        // 1. Coordenada
        Coordenada coord = new Coordenada();
        coord.setLatitud(request.getLatitud());
        coord.setLongitud(request.getLongitud());
        coord = coordenadaRepository.save(coord);

        // 2. Región y Comuna
        // Buscamos la región (Ej: "Metropolitana")
        Region region = regionRepository.findByNombre(request.getRegion())
            .orElseThrow(() -> new RuntimeException("Región no encontrada"));

        // Buscamos la comuna asegurándonos que exista
        Comuna comuna = comunaRepository.findByNombre(request.getComuna())
            .orElseThrow(() -> new RuntimeException("Comuna no encontrada"));

        // 3. Dirección
        Direccion dir = new Direccion();
        dir.setCalle(request.getCalle());
        dir.setNumero(request.getNumero());
        dir.setComuna(comuna);
        dir.setCoordenada(coord);
        dir = direccionRepository.save(dir);

        // 4. Recinto
        Recinto recinto = new Recinto();
        recinto.setNombre(request.getNombre());
        recinto.setRutEmpresa(request.getRutEmpresa());
        recinto.setDireccion(dir);

        return recintoRepository.save(recinto);
    }
}
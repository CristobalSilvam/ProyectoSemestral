package com.example.searchsport.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.searchsport.dto.RecintoMapaDTO;
import com.example.searchsport.dto.RecintoRequest;
import com.example.searchsport.entity.Comuna;
import com.example.searchsport.entity.Coordenada;
import com.example.searchsport.entity.Direccion;
import com.example.searchsport.entity.Recinto;
import com.example.searchsport.entity.Region;
import com.example.searchsport.repository.ComunaRepository;
import com.example.searchsport.repository.CoordenadaRepository;
import com.example.searchsport.repository.DireccionRepository;
import com.example.searchsport.repository.RecintoRepository;
import com.example.searchsport.repository.RegionRepository;

@Service
public class RecintoService {

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

    public List<RecintoMapaDTO> buscarParaMapa(String deporte, BigDecimal precioMax) {
        return recintoRepository.buscarRecintosParaMapa(deporte, precioMax);
    }

    public List<Recinto> obtenerTodos() {
        return recintoRepository.findAll();
    }

    public Optional<Recinto> obtenerPorId(Long id) {
        return recintoRepository.findById(id);
    }

    public Recinto guardar(Recinto recinto) {
    return recintoRepository.save(recinto);
}

    @Transactional
    public Recinto crearRecinto(RecintoRequest request) {
        Coordenada coord = new Coordenada();
        coord.setLatitud(request.getLatitud());
        coord.setLongitud(request.getLongitud());
        coord = coordenadaRepository.save(coord);

        Region region = regionRepository.findByNombre(request.getRegion())
                .orElseThrow(() -> new RuntimeException("Región no encontrada"));

        Comuna comuna = comunaRepository.findByNombre(request.getComuna())
                .orElseThrow(() -> new RuntimeException("Comuna no encontrada"));

        Direccion dir = new Direccion();
        dir.setCalle(request.getCalle());
        dir.setNumero(request.getNumero());
        dir.setComuna(comuna);
        dir.setCoordenada(coord);
        dir = direccionRepository.save(dir);

        Recinto recinto = new Recinto();
        recinto.setNombre(request.getNombre());
        recinto.setRutEmpresa(request.getRutEmpresa());
        recinto.setDireccion(dir);

        return recintoRepository.save(recinto);
    }
}
package com.example.searchsport.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; 

import com.example.searchsport.dto.RecintoRequest;
import com.example.searchsport.entity.Comuna;
import com.example.searchsport.entity.Direccion;
import com.example.searchsport.entity.Recinto;
import com.example.searchsport.repository.DireccionRepository;
import com.example.searchsport.repository.RecintoRepository;

@Service
public class RecintoService {

    @Autowired
    private RecintoRepository recintoRepository;

    @Autowired
    private DireccionRepository direccionRepository;


    public java.util.List<Recinto> obtenerTodos() {
        return recintoRepository.findAll();
    }

    @Transactional
    public Recinto crearRecinto(RecintoRequest request) {
        
        // 1. Creamos y guardamos la Dirección
        Direccion nuevaDireccion = new Direccion();
        nuevaDireccion.setCalle(request.getCalle());
        nuevaDireccion.setNumero(request.getNumero()); 
        
        // Creamos la referencia a la Comuna usando el ID que viene en el request
        Comuna comuna = new Comuna();
        comuna.setId(request.getComunaId());
        nuevaDireccion.setComuna(comuna);
        
        nuevaDireccion = direccionRepository.save(nuevaDireccion); 

        // 2. Creamos el Recinto
        Recinto nuevoRecinto = new Recinto();
        nuevoRecinto.setNombre(request.getNombre());
        nuevoRecinto.setRutEmpresa(request.getRutEmpresa());
        nuevoRecinto.setDireccion(nuevaDireccion);

        // 3. Guardamos
        return recintoRepository.save(nuevoRecinto);
    }
}
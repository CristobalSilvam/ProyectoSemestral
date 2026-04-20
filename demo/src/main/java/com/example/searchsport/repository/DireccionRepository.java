package com.example.searchsport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.searchsport.entity.Direccion;

@Repository
public interface DireccionRepository extends JpaRepository<Direccion, Integer> {
    //agregar métodos de búsqueda en el futuro, como buscar todas las direcciones de una comuna específica.
}
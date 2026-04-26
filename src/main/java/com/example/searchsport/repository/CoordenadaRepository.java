package com.example.searchsport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.searchsport.entity.Coordenada;

@Repository
public interface CoordenadaRepository extends JpaRepository<Coordenada, Long> {
}
package com.example.searchsport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.searchsport.entity.Deporte;

@Repository
public interface DeporteRepository extends JpaRepository<Deporte, Integer> {
}
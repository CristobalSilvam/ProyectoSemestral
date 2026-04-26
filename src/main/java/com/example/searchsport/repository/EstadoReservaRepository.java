package com.example.searchsport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.searchsport.entity.EstadoReserva;

@Repository
public interface EstadoReservaRepository extends JpaRepository<EstadoReserva, Long> {
}
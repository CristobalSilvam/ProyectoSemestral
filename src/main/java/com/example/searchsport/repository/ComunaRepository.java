package com.example.searchsport.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.searchsport.entity.Comuna;

@Repository
public interface ComunaRepository extends JpaRepository<Comuna, Long> {

    Optional<Comuna> findByNombre(String nombre);

    List<Comuna> findByRegion_Id(Long regionId);
}
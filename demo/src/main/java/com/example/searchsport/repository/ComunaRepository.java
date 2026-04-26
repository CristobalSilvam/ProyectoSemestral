package com.example.searchsport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.searchsport.entity.Comuna;
import java.util.Optional;

@Repository
public interface ComunaRepository extends JpaRepository<Comuna, Long> {
    
    // Este método es la magia que busca en la base de datos por el texto exacto
    Optional<Comuna> findByNombre(String nombre);
}
package com.example.searchsport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.searchsport.entity.Tarifa;
import java.util.List;

@Repository
public interface TarifaRepository extends JpaRepository<Tarifa, Long> {
    // Para buscar todas las tarifas de una cancha específica
    List<Tarifa> findByCanchaIdCancha(Long idCancha);
}
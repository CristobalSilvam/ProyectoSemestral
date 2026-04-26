package com.example.searchsport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.searchsport.entity.Recinto;
import com.example.searchsport.dto.RecintoMapaDTO;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface RecintoRepository extends JpaRepository<Recinto, Long> {
    @Query("SELECT new com.example.searchsport.dto.RecintoMapaDTO(" +
           "r.id, r.nombre, coord.latitud, coord.longitud, MIN(t.precio)) " +
           "FROM Tarifa t " +
           "JOIN t.cancha ca " +
           "JOIN ca.recinto r " +
           "JOIN r.direccion dir " +
           "JOIN dir.coordenada coord " +
           "JOIN ca.deporte dep " +
           "WHERE (:deporte IS NULL OR dep.nombre = :deporte) " +
           "AND (:precioMax IS NULL OR t.precio <= :precioMax) " +
           "GROUP BY r.id, r.nombre, coord.latitud, coord.longitud")
    List<RecintoMapaDTO> buscarRecintosParaMapa(
            @Param("deporte") String deporte, 
            @Param("precioMax") BigDecimal precioMax);
}
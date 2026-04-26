package com.example.searchsport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.searchsport.entity.HorarioEspecial;
import java.util.List;
import java.time.LocalDate;

@Repository
public interface HorarioEspecialRepository extends JpaRepository<HorarioEspecial, Long> {
    // Para saber si una cancha está bloqueada en una fecha puntual
    List<HorarioEspecial> findByCanchaIdCanchaAndFecha(Long idCancha, LocalDate fecha);

    // Para listar todos los bloqueos de una cancha específica
    List<HorarioEspecial> findByCanchaIdCancha(Long idCancha);
}
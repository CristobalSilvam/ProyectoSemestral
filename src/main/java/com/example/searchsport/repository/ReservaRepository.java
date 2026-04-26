package com.example.searchsport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.searchsport.entity.Reserva;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDate;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    
    // Para el historial del usuario (Tus reservas pasadas y futuras)
    List<Reserva> findByUsuarioId(Long idUsuario);

    // Para ver qué horas ya están tomadas en una cancha un día específico
    List<Reserva> findByCanchaIdCanchaAndFechaUso(Long idCancha, LocalDate fechaUso);
    
    // Para el Dashboard del dueño: Ver ingresos de reservas "Pagadas"
    List<Reserva> findByCanchaRecintoIdAndEstadoIdEstado(Long idRecinto, Long idEstado);

    // Sumar los ingresos (Solo reservas Pagadas -> id_estado = 2)
    @Query("SELECT SUM(r.montoTotal) FROM Reserva r JOIN r.cancha c WHERE c.recinto.id = :recintoId AND r.estado.idEstado = 2L AND MONTH(r.fechaUso) = :mes AND YEAR(r.fechaUso) = :anio")
    BigDecimal calcularIngresosPorMesYRecinto(@Param("recintoId") Long recintoId, @Param("mes") int mes, @Param("anio") int anio);

    // Extra: Contar cuántas reservas pagadas hubo en ese mes
    @Query("SELECT COUNT(r) FROM Reserva r JOIN r.cancha c WHERE c.recinto.id = :recintoId AND r.estado.idEstado = 2L AND MONTH(r.fechaUso) = :mes AND YEAR(r.fechaUso) = :anio")
    Long contarReservasPagadasPorMesYRecinto(@Param("recintoId") Long recintoId, @Param("mes") int mes, @Param("anio") int anio);

    // Calcular el promedio de estrellas de un recinto
    @Query("SELECT AVG(r.review.puntaje) FROM Reserva r WHERE r.cancha.recinto.id = :recintoId AND r.review IS NOT NULL")
    Double calcularPromedioEstrellasPorRecinto(@Param("recintoId") Long recintoId);

    // Contar cuántas reseñas tiene un recinto
    @Query("SELECT COUNT(r.review) FROM Reserva r WHERE r.cancha.recinto.id = :recintoId AND r.review IS NOT NULL")
    Long contarReviewsPorRecinto(@Param("recintoId") Long recintoId);
}
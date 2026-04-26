package com.example.searchsport.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "RESERVA")
@Data
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Long idReserva;

    @Column(name = "fecha_uso", nullable = false)
    private LocalDate fechaUso;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @Column(name = "monto_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoTotal;

    @ManyToOne
    @JoinColumn(name = "cancha_id", nullable = false)
    private Cancha cancha;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "estado_id", nullable = false)
    private EstadoReserva estado;

    @OneToOne // Relación uno a uno con Review
    @JoinColumn(name = "review_id", unique = true)
    private Review review;
}
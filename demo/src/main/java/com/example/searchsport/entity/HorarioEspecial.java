package com.example.searchsport.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "HORARIO_ESPECIAL")
@Data
public class HorarioEspecial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_he")
    private Long idHe;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(length = 100)
    private String motivo;

    @Column(name = "esta_bloqueado")
    private Boolean estaBloqueado = true;

    @ManyToOne
    @JoinColumn(name = "cancha_id", nullable = false)
    private Cancha cancha;
}
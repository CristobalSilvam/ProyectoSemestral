package com.example.searchsport.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "ESTADO_RESERVA")
@Data
public class EstadoReserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estado")
    private Long idEstado;

    @Column(nullable = false, length = 50)
    private String descripcion;
}
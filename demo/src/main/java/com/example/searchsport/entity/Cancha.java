package com.example.searchsport.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity

@Table(name = "CANCHA")
@Data
public class Cancha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cancha")
    private Long idCancha;

    @Column(name = "nombre_interno", nullable = false, length = 50)
    private String nombreInterno;

    @Column(name = "es_techada")
    private Boolean esTechada = false;

    @Column(name = "tipo_superficie", length = 30)
    private String tipoSuperficie;

    @ManyToOne
    @JoinColumn(name = "recinto_id", nullable = false)
    private Recinto recinto;

    @ManyToOne
    @JoinColumn(name = "deporte_id", nullable = false)
    private Deporte deporte;
}
package com.example.searchsport.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "recinto")
@Data
public class Recinto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(name = "rut_empresa", nullable = false, length = 20)
    private String rutEmpresa;

    @Column(name = "aprobado")
    private Boolean aprobado = false;

    @ManyToOne
    @JoinColumn(name = "direccion_id", nullable = false)
    private Direccion direccion;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    @JsonIgnore
    private Usuario usuario;

    @OneToMany(mappedBy = "recinto")
    @JsonIgnoreProperties("recinto")
    private List<Cancha> canchas;
}
package com.example.searchsport.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "TELEFONO")
@Data
public class Telefono {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_telefono")
    private Long idTelefono;

    @Column(nullable = false, length = 20)
    private String numero;

    @Column(length = 20)
    private String tipo;

    @ManyToOne
    @JoinColumn(name = "recinto_id")
    private Recinto recinto;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
}
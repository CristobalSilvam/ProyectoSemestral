package com.example.searchsport.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "EMAIL")
@Data
public class Email {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_email")
    private Long idEmail;

    @Column(nullable = false, length = 100)
    private String correo;

    @Column(name = "es_principal")
    private Boolean esPrincipal = false;

    @ManyToOne
    @JoinColumn(name = "recinto_id")
    private Recinto recinto;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
}
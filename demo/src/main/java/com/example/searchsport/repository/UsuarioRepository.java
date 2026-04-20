package com.example.searchsport.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.searchsport.entity.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    // SELECT * FROM usuario WHERE email = ?
    Optional<Usuario> findByEmail(String email);

    // encontrar usuario por rut
    Optional<Usuario> findByRut(String rut);
}
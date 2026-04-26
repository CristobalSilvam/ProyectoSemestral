package com.example.searchsport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.searchsport.entity.Rol;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
}
package com.example.searchsport.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.searchsport.entity.Cancha;

@Repository
public interface CanchaRepository extends JpaRepository<Cancha, Integer> {
    List<Cancha> findByRecintoId(Integer recintoId);
}
package com.example.searchsport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.searchsport.entity.Telefono;
import java.util.List;

@Repository
public interface TelefonoRepository extends JpaRepository<Telefono, Long> {
    List<Telefono> findByRecintoId(Long recintoId);
}
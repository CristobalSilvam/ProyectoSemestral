package com.example.searchsport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.searchsport.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
}
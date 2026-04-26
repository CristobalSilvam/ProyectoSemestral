package com.example.searchsport.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.searchsport.dto.ReputacionDTO;
import com.example.searchsport.dto.ReviewRequest;
import com.example.searchsport.entity.Reserva;
import com.example.searchsport.entity.Review;
import com.example.searchsport.repository.ReservaRepository;
import com.example.searchsport.repository.ReviewRepository;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Transactional
    public Review dejarReview(ReviewRequest request, String emailUsuario) {
        
        // 1. Buscar la reserva
        Reserva reserva = reservaRepository.findById(request.getReservaId())
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        // 2. Validar que la reserva pertenezca a quien intenta comentar
        if (!reserva.getUsuario().getEmail().equals(emailUsuario)) {
            throw new RuntimeException("No puedes calificar una reserva que no hiciste tú.");
        }

        // 3. Validar que esté Pagada (estado = 2). 
        if (reserva.getEstado().getIdEstado() != 2L) {
            throw new RuntimeException("Solo puedes dejar una reseña para reservas pagadas y finalizadas.");
        }

        // 4. Validar que no haya comentado antes (Relación 1 a 1)
        if (reserva.getReview() != null) {
            throw new RuntimeException("Ya dejaste una reseña para esta reserva anteriormente.");
        }

        // 5. Validar el rango de estrellas
        if (request.getPuntaje() < 1 || request.getPuntaje() > 5) {
            throw new RuntimeException("El puntaje debe estar entre 1 y 5 estrellas.");
        }

        // 6. Crear y guardar la Review
        Review review = new Review();
        review.setPuntaje(request.getPuntaje());
        review.setComentario(request.getComentario());
        review = reviewRepository.save(review);

        // 7. Vincularla a la Reserva y actualizar
        reserva.setReview(review);
        reservaRepository.save(reserva);

        return review;
    }

    public ReputacionDTO obtenerReputacionRecinto(Long recintoId) {
        Double promedio = reservaRepository.calcularPromedioEstrellasPorRecinto(recintoId);
        Long total = reservaRepository.contarReviewsPorRecinto(recintoId);

        // Redondear a 1 decimal (ej: 4.5) y manejar casos donde no hay reseñas aún
        Double promedioRedondeado = (promedio != null) ? Math.round(promedio * 10.0) / 10.0 : 0.0;
        Long totalReal = (total != null) ? total : 0L;

        return new ReputacionDTO(recintoId, promedioRedondeado, totalReal);
    }
}
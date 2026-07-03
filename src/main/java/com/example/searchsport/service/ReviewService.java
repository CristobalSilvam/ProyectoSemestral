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

    private static final Long ESTADO_PAGADA_ID = 2L;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Transactional
    public Review dejarReview(ReviewRequest request, String emailUsuario) {

        Reserva reserva = reservaRepository.findById(request.getReservaId())
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        if (reserva.getUsuario() == null || reserva.getUsuario().getEmail() == null) {
            throw new RuntimeException("La reserva no tiene un usuario válido.");
        }

        if (!reserva.getUsuario().getEmail().equals(emailUsuario)) {
            throw new RuntimeException("No puedes calificar una reserva que no hiciste tú.");
        }

        if (reserva.getEstado() == null || !ESTADO_PAGADA_ID.equals(reserva.getEstado().getIdEstado())) {
            throw new RuntimeException("Solo puedes dejar una reseña para reservas pagadas y finalizadas.");
        }

        if (reserva.getReview() != null) {
            throw new RuntimeException("Ya dejaste una reseña para esta reserva anteriormente.");
        }

        if (request.getPuntaje() < 1 || request.getPuntaje() > 5) {
            throw new RuntimeException("El puntaje debe estar entre 1 y 5 estrellas.");
        }

        Review review = new Review();
        review.setPuntaje(request.getPuntaje());
        review.setComentario(request.getComentario());

        review = reviewRepository.save(review);

        reserva.setReview(review);
        reservaRepository.save(reserva);

        return review;
    }

    public ReputacionDTO obtenerReputacionRecinto(Long recintoId) {
        Double promedio = reservaRepository.calcularPromedioEstrellasPorRecinto(recintoId);
        Long total = reservaRepository.contarReviewsPorRecinto(recintoId);

        Double promedioRedondeado = promedio != null
                ? Math.round(promedio * 10.0) / 10.0
                : 0.0;

        Long totalReal = total != null ? total : 0L;

        return new ReputacionDTO(recintoId, promedioRedondeado, totalReal);
    }
}
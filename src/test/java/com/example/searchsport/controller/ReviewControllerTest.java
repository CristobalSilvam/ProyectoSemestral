package com.example.searchsport.controller;

import com.example.searchsport.dto.ReputacionDTO;
import com.example.searchsport.dto.ReviewRequest;
import com.example.searchsport.entity.Review;
import com.example.searchsport.service.ReviewService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReviewControllerTest {

    @AfterEach
    void limpiarContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void publicarReview_debeRetornarOkCuandoReviewSeCreaCorrectamente() {
        ReviewService reviewService = mock(ReviewService.class);
        ReviewController controller = crearController(reviewService);

        autenticar("cliente@email.com");

        ReviewRequest request = new ReviewRequest();
        request.setReservaId(1L);
        request.setPuntaje((byte) 5);
        request.setComentario("Excelente cancha");

        Review review = new Review();
        review.setIdReview(10L);
        review.setPuntaje((byte) 5);
        review.setComentario("Excelente cancha");

        when(reviewService.dejarReview(request, "cliente@email.com")).thenReturn(review);

        ResponseEntity<?> response = controller.publicarReview(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(review, response.getBody());

        verify(reviewService).dejarReview(request, "cliente@email.com");
    }

    @Test
    void publicarReview_debeRetornarBadRequestCuandoServiceLanzaRuntimeException() {
        ReviewService reviewService = mock(ReviewService.class);
        ReviewController controller = crearController(reviewService);

        autenticar("cliente@email.com");

        ReviewRequest request = new ReviewRequest();
        request.setReservaId(1L);
        request.setPuntaje((byte) 6);
        request.setComentario("Muy buena");

        when(reviewService.dejarReview(request, "cliente@email.com"))
                .thenThrow(new RuntimeException("El puntaje debe estar entre 1 y 5 estrellas."));

        ResponseEntity<?> response = controller.publicarReview(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("El puntaje debe estar entre 1 y 5 estrellas.", response.getBody());

        verify(reviewService).dejarReview(request, "cliente@email.com");
    }

    @Test
    void verReputacion_debeRetornarReputacionDelRecinto() {
        ReviewService reviewService = mock(ReviewService.class);
        ReviewController controller = crearController(reviewService);

        ReputacionDTO reputacion = new ReputacionDTO(1L, 4.5, 8L);

        when(reviewService.obtenerReputacionRecinto(1L)).thenReturn(reputacion);

        ResponseEntity<ReputacionDTO> response = controller.verReputacion(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(reputacion, response.getBody());

        verify(reviewService).obtenerReputacionRecinto(1L);
    }

    private ReviewController crearController(ReviewService reviewService) {
        ReviewController controller = new ReviewController();

        try {
            Field field = ReviewController.class.getDeclaredField("reviewService");
            field.setAccessible(true);
            field.set(controller, reviewService);
            return controller;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void autenticar(String email) {
        TestingAuthenticationToken authentication = new TestingAuthenticationToken(email, null);
        authentication.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
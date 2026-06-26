package com.example.searchsport.service;

import com.example.searchsport.dto.ReputacionDTO;
import com.example.searchsport.dto.ReviewRequest;
import com.example.searchsport.entity.EstadoReserva;
import com.example.searchsport.entity.Reserva;
import com.example.searchsport.entity.Review;
import com.example.searchsport.entity.Usuario;
import com.example.searchsport.repository.ReservaRepository;
import com.example.searchsport.repository.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReservaRepository reservaRepository;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    void dejarReview_debeCrearReviewCorrectamente() {
        ReviewRequest request = crearRequest(1L, (byte) 5, "Excelente cancha");
        Reserva reserva = crearReserva("cliente@email.com", 2L, null);

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> {
            Review review = invocation.getArgument(0);
            review.setIdReview(10L);
            return review;
        });
        when(reservaRepository.save(reserva)).thenReturn(reserva);

        Review resultado = reviewService.dejarReview(request, "cliente@email.com");

        assertNotNull(resultado);
        assertEquals(10L, resultado.getIdReview());
        assertEquals(Byte.valueOf((byte) 5), resultado.getPuntaje());
        assertEquals("Excelente cancha", resultado.getComentario());
        assertEquals(resultado, reserva.getReview());

        verify(reservaRepository).findById(1L);
        verify(reviewRepository).save(any(Review.class));
        verify(reservaRepository).save(reserva);
    }

    @Test
    void dejarReview_debeLanzarExcepcionSiReservaNoExiste() {
        ReviewRequest request = crearRequest(99L, (byte) 5, "Buena");

        when(reservaRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> reviewService.dejarReview(request, "cliente@email.com")
        );

        assertEquals("Reserva no encontrada", exception.getMessage());
        verify(reviewRepository, never()).save(any(Review.class));
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void dejarReview_debeLanzarExcepcionSiReservaNoPerteneceAlUsuario() {
        ReviewRequest request = crearRequest(1L, (byte) 5, "Buena");
        Reserva reserva = crearReserva("otro@email.com", 2L, null);

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> reviewService.dejarReview(request, "cliente@email.com")
        );

        assertEquals("No puedes calificar una reserva que no hiciste tú.", exception.getMessage());
        verify(reviewRepository, never()).save(any(Review.class));
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void dejarReview_debeLanzarExcepcionSiReservaNoEstaPagada() {
        ReviewRequest request = crearRequest(1L, (byte) 5, "Buena");
        Reserva reserva = crearReserva("cliente@email.com", 1L, null);

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> reviewService.dejarReview(request, "cliente@email.com")
        );

        assertEquals("Solo puedes dejar una reseña para reservas pagadas y finalizadas.", exception.getMessage());
        verify(reviewRepository, never()).save(any(Review.class));
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void dejarReview_debeLanzarExcepcionSiYaTieneReview() {
        ReviewRequest request = crearRequest(1L, (byte) 5, "Buena");

        Review reviewExistente = new Review();
        reviewExistente.setIdReview(7L);

        Reserva reserva = crearReserva("cliente@email.com", 2L, reviewExistente);

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> reviewService.dejarReview(request, "cliente@email.com")
        );

        assertEquals("Ya dejaste una reseña para esta reserva anteriormente.", exception.getMessage());
        verify(reviewRepository, never()).save(any(Review.class));
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void dejarReview_debeLanzarExcepcionSiPuntajeEsMenorAUno() {
        ReviewRequest request = crearRequest(1L, (byte) 0, "Mala");
        Reserva reserva = crearReserva("cliente@email.com", 2L, null);

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> reviewService.dejarReview(request, "cliente@email.com")
        );

        assertEquals("El puntaje debe estar entre 1 y 5 estrellas.", exception.getMessage());
        verify(reviewRepository, never()).save(any(Review.class));
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void dejarReview_debeLanzarExcepcionSiPuntajeEsMayorACinco() {
        ReviewRequest request = crearRequest(1L, (byte) 6, "Muy buena");
        Reserva reserva = crearReserva("cliente@email.com", 2L, null);

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> reviewService.dejarReview(request, "cliente@email.com")
        );

        assertEquals("El puntaje debe estar entre 1 y 5 estrellas.", exception.getMessage());
        verify(reviewRepository, never()).save(any(Review.class));
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void obtenerReputacionRecinto_debeRetornarPromedioRedondeadoYTotal() {
        when(reservaRepository.calcularPromedioEstrellasPorRecinto(1L)).thenReturn(4.56);
        when(reservaRepository.contarReviewsPorRecinto(1L)).thenReturn(12L);

        ReputacionDTO resultado = reviewService.obtenerReputacionRecinto(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getRecintoId());
        assertEquals(4.6, resultado.getPromedioEstrellas());

        verify(reservaRepository).calcularPromedioEstrellasPorRecinto(1L);
        verify(reservaRepository).contarReviewsPorRecinto(1L);
    }

    @Test
    void obtenerReputacionRecinto_debeRetornarCeroSiNoHayReviews() {
        when(reservaRepository.calcularPromedioEstrellasPorRecinto(1L)).thenReturn(null);
        when(reservaRepository.contarReviewsPorRecinto(1L)).thenReturn(null);

        ReputacionDTO resultado = reviewService.obtenerReputacionRecinto(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getRecintoId());
        assertEquals(0.0, resultado.getPromedioEstrellas());

        verify(reservaRepository).calcularPromedioEstrellasPorRecinto(1L);
        verify(reservaRepository).contarReviewsPorRecinto(1L);
    }

    private ReviewRequest crearRequest(Long reservaId, Byte puntaje, String comentario) {
        ReviewRequest request = new ReviewRequest();
        request.setReservaId(reservaId);
        request.setPuntaje(puntaje);
        request.setComentario(comentario);
        return request;
    }

    private Reserva crearReserva(String emailUsuario, Long estadoId, Review review) {
        Usuario usuario = new Usuario();
        usuario.setEmail(emailUsuario);

        EstadoReserva estado = new EstadoReserva();
        estado.setIdEstado(estadoId);

        Reserva reserva = new Reserva();
        reserva.setIdReserva(1L);
        reserva.setUsuario(usuario);
        reserva.setEstado(estado);
        reserva.setReview(review);

        return reserva;
    }
}
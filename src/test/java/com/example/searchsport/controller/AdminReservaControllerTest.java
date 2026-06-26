package com.example.searchsport.controller;

import com.example.searchsport.entity.Cancha;
import com.example.searchsport.entity.Deporte;
import com.example.searchsport.entity.EstadoReserva;
import com.example.searchsport.entity.Recinto;
import com.example.searchsport.entity.Reserva;
import com.example.searchsport.entity.Usuario;
import com.example.searchsport.repository.EstadoReservaRepository;
import com.example.searchsport.repository.ReservaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminReservaControllerTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private EstadoReservaRepository estadoReservaRepository;

    @InjectMocks
    private AdminReservaController adminReservaController;

    @Test
    void listarReservas_debeRetornarReservas() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Cristobal");
        usuario.setSegundoNombre("Andres");
        usuario.setApellidoPaterno("Silva");
        usuario.setApellidoMaterno("Perez");
        usuario.setEmail("cristobal@email.com");

        Recinto recinto = new Recinto();
        recinto.setId(1L);
        recinto.setNombre("Complejo Deportivo Central");

        Deporte deporte = new Deporte();
        deporte.setNombre("Fútbol");

        Cancha cancha = new Cancha();
        cancha.setIdCancha(1L);
        cancha.setNombreInterno("Cancha 1");
        cancha.setEsTechada(true);
        cancha.setTipoSuperficie("Pasto sintético");
        cancha.setRecinto(recinto);
        cancha.setDeporte(deporte);

        EstadoReserva estado = new EstadoReserva();
        estado.setIdEstado(2L);
        estado.setDescripcion("Pagada");

        Reserva reserva = new Reserva();
        reserva.setIdReserva(1L);
        reserva.setFechaUso(LocalDate.of(2026, 6, 25));
        reserva.setHoraInicio(LocalTime.of(20, 0));
        reserva.setHoraFin(LocalTime.of(21, 0));
        reserva.setMontoTotal(new BigDecimal("30000"));
        reserva.setUsuario(usuario);
        reserva.setCancha(cancha);
        reserva.setEstado(estado);

        when(reservaRepository.findAll()).thenReturn(List.of(reserva));

        ResponseEntity<List<AdminReservaController.ReservaResponse>> response =
                adminReservaController.listarReservas();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());

        AdminReservaController.ReservaResponse reservaResponse = response.getBody().get(0);

        assertEquals(1L, reservaResponse.getId());
        assertEquals(LocalDate.of(2026, 6, 25), reservaResponse.getFechaUso());
        assertEquals(LocalTime.of(20, 0), reservaResponse.getHoraInicio());
        assertEquals(LocalTime.of(21, 0), reservaResponse.getHoraFin());
        assertEquals(new BigDecimal("30000"), reservaResponse.getMontoTotal());

        assertEquals(1L, reservaResponse.getUsuarioId());
        assertEquals("Cristobal Andres Silva Perez", reservaResponse.getUsuarioNombre());
        assertEquals("cristobal@email.com", reservaResponse.getUsuarioEmail());

        assertEquals(1L, reservaResponse.getCanchaId());
        assertEquals("Cancha 1", reservaResponse.getCanchaNombre());
        assertTrue(reservaResponse.getCanchaTechada());
        assertEquals("Pasto sintético", reservaResponse.getCanchaSuperficie());

        assertEquals(1L, reservaResponse.getRecintoId());
        assertEquals("Complejo Deportivo Central", reservaResponse.getRecintoNombre());

        assertEquals("Fútbol", reservaResponse.getDeporteNombre());

        assertEquals(2L, reservaResponse.getEstadoId());
        assertEquals("Pagada", reservaResponse.getEstadoDescripcion());

        verify(reservaRepository, times(1)).findAll();
    }

    @Test
    void listarEstados_debeRetornarEstados() {
        EstadoReserva estadoPendiente = new EstadoReserva();
        estadoPendiente.setIdEstado(1L);
        estadoPendiente.setDescripcion("Pendiente");

        EstadoReserva estadoPagada = new EstadoReserva();
        estadoPagada.setIdEstado(2L);
        estadoPagada.setDescripcion("Pagada");

        when(estadoReservaRepository.findAll())
                .thenReturn(List.of(estadoPendiente, estadoPagada));

        ResponseEntity<List<AdminReservaController.EstadoReservaResponse>> response =
                adminReservaController.listarEstados();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());

        assertEquals(1L, response.getBody().get(0).getId());
        assertEquals("Pendiente", response.getBody().get(0).getDescripcion());

        assertEquals(2L, response.getBody().get(1).getId());
        assertEquals("Pagada", response.getBody().get(1).getDescripcion());

        verify(estadoReservaRepository, times(1)).findAll();
    }

    @Test
    void cambiarEstado_debeActualizarEstadoDeReserva() {
        EstadoReserva estadoPendiente = new EstadoReserva();
        estadoPendiente.setIdEstado(1L);
        estadoPendiente.setDescripcion("Pendiente");

        EstadoReserva estadoCancelada = new EstadoReserva();
        estadoCancelada.setIdEstado(3L);
        estadoCancelada.setDescripcion("Cancelada");

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Cristobal");
        usuario.setApellidoPaterno("Silva");
        usuario.setEmail("cristobal@email.com");

        Recinto recinto = new Recinto();
        recinto.setId(1L);
        recinto.setNombre("Complejo Deportivo Central");

        Deporte deporte = new Deporte();
        deporte.setNombre("Fútbol");

        Cancha cancha = new Cancha();
        cancha.setIdCancha(1L);
        cancha.setNombreInterno("Cancha 1");
        cancha.setEsTechada(false);
        cancha.setTipoSuperficie("Pasto sintético");
        cancha.setRecinto(recinto);
        cancha.setDeporte(deporte);

        Reserva reserva = new Reserva();
        reserva.setIdReserva(1L);
        reserva.setFechaUso(LocalDate.of(2026, 6, 25));
        reserva.setHoraInicio(LocalTime.of(20, 0));
        reserva.setHoraFin(LocalTime.of(21, 0));
        reserva.setMontoTotal(new BigDecimal("30000"));
        reserva.setUsuario(usuario);
        reserva.setCancha(cancha);
        reserva.setEstado(estadoPendiente);

        AdminReservaController.CambiarEstadoRequest request =
                new AdminReservaController.CambiarEstadoRequest();

        request.setEstadoId(3L);

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(estadoReservaRepository.findById(3L)).thenReturn(Optional.of(estadoCancelada));
        when(reservaRepository.save(reserva)).thenReturn(reserva);

        ResponseEntity<AdminReservaController.ReservaResponse> response =
                adminReservaController.cambiarEstado(1L, request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());

        assertEquals(3L, response.getBody().getEstadoId());
        assertEquals("Cancelada", response.getBody().getEstadoDescripcion());

        verify(reservaRepository, times(1)).findById(1L);
        verify(estadoReservaRepository, times(1)).findById(3L);
        verify(reservaRepository, times(1)).save(reserva);
    }

    @Test
    void cambiarEstado_debeLanzarExcepcionSiReservaNoExiste() {
        AdminReservaController.CambiarEstadoRequest request =
                new AdminReservaController.CambiarEstadoRequest();

        request.setEstadoId(2L);

        when(reservaRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> adminReservaController.cambiarEstado(99L, request)
        );

        assertEquals("Reserva no encontrada", exception.getMessage());

        verify(reservaRepository, times(1)).findById(99L);
        verify(estadoReservaRepository, never()).findById(anyLong());
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void cambiarEstado_debeLanzarExcepcionSiEstadoNoExiste() {
        Reserva reserva = new Reserva();
        reserva.setIdReserva(1L);
        reserva.setFechaUso(LocalDate.of(2026, 6, 25));
        reserva.setHoraInicio(LocalTime.of(20, 0));
        reserva.setHoraFin(LocalTime.of(21, 0));
        reserva.setMontoTotal(new BigDecimal("30000"));

        AdminReservaController.CambiarEstadoRequest request =
                new AdminReservaController.CambiarEstadoRequest();

        request.setEstadoId(99L);

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(estadoReservaRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> adminReservaController.cambiarEstado(1L, request)
        );

        assertEquals("Estado de reserva no encontrado", exception.getMessage());

        verify(reservaRepository, times(1)).findById(1L);
        verify(estadoReservaRepository, times(1)).findById(99L);
        verify(reservaRepository, never()).save(any(Reserva.class));
    }
}
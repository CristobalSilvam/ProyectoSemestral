package com.example.searchsport.service;

import com.example.searchsport.dto.ReservaRequest;
import com.example.searchsport.entity.Cancha;
import com.example.searchsport.entity.EstadoReserva;
import com.example.searchsport.entity.Reserva;
import com.example.searchsport.entity.Usuario;
import com.example.searchsport.repository.ReservaRepository;
import com.example.searchsport.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private ReservaService reservaService;

    @Test
    void crearReserva_debeCrearReservaCuandoNoExisteConflictoHorario() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("cliente@email.com");

        ReservaRequest request = new ReservaRequest();
        request.setCanchaId(1L);
        request.setFechaUso(LocalDate.of(2026, 7, 10));
        request.setHoraInicio(LocalTime.of(20, 0));
        request.setHoraFin(LocalTime.of(21, 0));
        request.setMontoTotal(new BigDecimal("30000"));

        when(usuarioRepository.findByEmail("cliente@email.com"))
                .thenReturn(Optional.of(usuario));

        when(reservaRepository.findByCanchaIdCanchaAndFechaUso(
                1L,
                LocalDate.of(2026, 7, 10)
        )).thenReturn(List.of());

        when(reservaRepository.save(any(Reserva.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Reserva reservaCreada = reservaService.crearReserva(request, "cliente@email.com");

        assertNotNull(reservaCreada);
        assertEquals(LocalDate.of(2026, 7, 10), reservaCreada.getFechaUso());
        assertEquals(LocalTime.of(20, 0), reservaCreada.getHoraInicio());
        assertEquals(LocalTime.of(21, 0), reservaCreada.getHoraFin());
        assertEquals(new BigDecimal("30000"), reservaCreada.getMontoTotal());

        assertNotNull(reservaCreada.getCancha());
        assertEquals(1L, reservaCreada.getCancha().getIdCancha());

        assertNotNull(reservaCreada.getUsuario());
        assertEquals("cliente@email.com", reservaCreada.getUsuario().getEmail());

        assertNotNull(reservaCreada.getEstado());
        assertEquals(1L, reservaCreada.getEstado().getIdEstado());

        verify(usuarioRepository, times(1)).findByEmail("cliente@email.com");
        verify(reservaRepository, times(1))
                .findByCanchaIdCanchaAndFechaUso(1L, LocalDate.of(2026, 7, 10));
        verify(reservaRepository, times(1)).save(any(Reserva.class));
    }

    @Test
    void crearReserva_debeLanzarExcepcionSiUsuarioNoExiste() {
        ReservaRequest request = new ReservaRequest();
        request.setCanchaId(1L);
        request.setFechaUso(LocalDate.of(2026, 7, 10));
        request.setHoraInicio(LocalTime.of(20, 0));
        request.setHoraFin(LocalTime.of(21, 0));
        request.setMontoTotal(new BigDecimal("30000"));

        when(usuarioRepository.findByEmail("noexiste@email.com"))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> reservaService.crearReserva(request, "noexiste@email.com")
        );

        assertEquals("Usuario no encontrado en la base de datos", exception.getMessage());

        verify(usuarioRepository, times(1)).findByEmail("noexiste@email.com");
        verify(reservaRepository, never()).findByCanchaIdCanchaAndFechaUso(anyLong(), any(LocalDate.class));
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void crearReserva_debeLanzarExcepcionSiHorarioTieneMismaHoraDeInicio() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("cliente@email.com");

        ReservaRequest request = new ReservaRequest();
        request.setCanchaId(1L);
        request.setFechaUso(LocalDate.of(2026, 7, 10));
        request.setHoraInicio(LocalTime.of(20, 0));
        request.setHoraFin(LocalTime.of(21, 0));
        request.setMontoTotal(new BigDecimal("30000"));

        Reserva reservaExistente = new Reserva();
        reservaExistente.setHoraInicio(LocalTime.of(20, 0));
        reservaExistente.setHoraFin(LocalTime.of(21, 0));

        when(usuarioRepository.findByEmail("cliente@email.com"))
                .thenReturn(Optional.of(usuario));

        when(reservaRepository.findByCanchaIdCanchaAndFechaUso(
                1L,
                LocalDate.of(2026, 7, 10)
        )).thenReturn(List.of(reservaExistente));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> reservaService.crearReserva(request, "cliente@email.com")
        );

        assertEquals("Lo sentimos, este bloque horario acaba de ser reservado por alguien más.", exception.getMessage());

        verify(usuarioRepository, times(1)).findByEmail("cliente@email.com");
        verify(reservaRepository, times(1))
                .findByCanchaIdCanchaAndFechaUso(1L, LocalDate.of(2026, 7, 10));
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void crearReserva_debeLanzarExcepcionSiHoraInicioEstaDentroDeOtraReserva() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("cliente@email.com");

        ReservaRequest request = new ReservaRequest();
        request.setCanchaId(1L);
        request.setFechaUso(LocalDate.of(2026, 7, 10));
        request.setHoraInicio(LocalTime.of(20, 30));
        request.setHoraFin(LocalTime.of(21, 30));
        request.setMontoTotal(new BigDecimal("30000"));

        Reserva reservaExistente = new Reserva();
        reservaExistente.setHoraInicio(LocalTime.of(20, 0));
        reservaExistente.setHoraFin(LocalTime.of(21, 0));

        when(usuarioRepository.findByEmail("cliente@email.com"))
                .thenReturn(Optional.of(usuario));

        when(reservaRepository.findByCanchaIdCanchaAndFechaUso(
                1L,
                LocalDate.of(2026, 7, 10)
        )).thenReturn(List.of(reservaExistente));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> reservaService.crearReserva(request, "cliente@email.com")
        );

        assertEquals("Lo sentimos, este bloque horario acaba de ser reservado por alguien más.", exception.getMessage());

        verify(usuarioRepository, times(1)).findByEmail("cliente@email.com");
        verify(reservaRepository, times(1))
                .findByCanchaIdCanchaAndFechaUso(1L, LocalDate.of(2026, 7, 10));
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void confirmarPago_debeCambiarEstadoAPagada() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("cliente@email.com");

        Reserva reserva = new Reserva();
        reserva.setIdReserva(1L);
        reserva.setUsuario(usuario);

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(reservaRepository.save(reserva)).thenReturn(reserva);

        Reserva reservaPagada = reservaService.confirmarPago(1L, "cliente@email.com");

        assertNotNull(reservaPagada);
        assertNotNull(reservaPagada.getEstado());
        assertEquals(2L, reservaPagada.getEstado().getIdEstado());

        verify(reservaRepository, times(1)).findById(1L);
        verify(reservaRepository, times(1)).save(reserva);
    }

    @Test
    void confirmarPago_debeLanzarExcepcionSiReservaNoExiste() {
        when(reservaRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> reservaService.confirmarPago(99L, "cliente@email.com")
        );

        assertEquals("Reserva no encontrada", exception.getMessage());

        verify(reservaRepository, times(1)).findById(99L);
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void confirmarPago_debeLanzarExcepcionSiUsuarioNoEsDuenioDeLaReserva() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("otro@email.com");

        Reserva reserva = new Reserva();
        reserva.setIdReserva(1L);
        reserva.setUsuario(usuario);

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> reservaService.confirmarPago(1L, "cliente@email.com")
        );

        assertEquals("No tienes permiso para pagar esta reserva", exception.getMessage());

        verify(reservaRepository, times(1)).findById(1L);
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void cancelarReserva_debeCambiarEstadoACanceladaSiTieneMasDe24Horas() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("cliente@email.com");

        LocalDateTime fechaInicio = LocalDateTime.now().plusDays(3);

        Reserva reserva = new Reserva();
        reserva.setIdReserva(1L);
        reserva.setUsuario(usuario);
        reserva.setFechaUso(fechaInicio.toLocalDate());
        reserva.setHoraInicio(fechaInicio.toLocalTime());
        reserva.setHoraFin(fechaInicio.plusHours(1).toLocalTime());

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(reservaRepository.save(reserva)).thenReturn(reserva);

        Reserva reservaCancelada = reservaService.cancelarReserva(1L, "cliente@email.com");

        assertNotNull(reservaCancelada);
        assertNotNull(reservaCancelada.getEstado());
        assertEquals(3L, reservaCancelada.getEstado().getIdEstado());

        verify(reservaRepository, times(1)).findById(1L);
        verify(reservaRepository, times(1)).save(reserva);
    }

    @Test
    void cancelarReserva_debeLanzarExcepcionSiReservaNoExiste() {
        when(reservaRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> reservaService.cancelarReserva(99L, "cliente@email.com")
        );

        assertEquals("Reserva no encontrada", exception.getMessage());

        verify(reservaRepository, times(1)).findById(99L);
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void cancelarReserva_debeLanzarExcepcionSiUsuarioNoEsDuenioDeLaReserva() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("otro@email.com");

        LocalDateTime fechaInicio = LocalDateTime.now().plusDays(3);

        Reserva reserva = new Reserva();
        reserva.setIdReserva(1L);
        reserva.setUsuario(usuario);
        reserva.setFechaUso(fechaInicio.toLocalDate());
        reserva.setHoraInicio(fechaInicio.toLocalTime());
        reserva.setHoraFin(fechaInicio.plusHours(1).toLocalTime());

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> reservaService.cancelarReserva(1L, "cliente@email.com")
        );

        assertEquals("No tienes permiso para cancelar esta reserva", exception.getMessage());

        verify(reservaRepository, times(1)).findById(1L);
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void cancelarReserva_debeLanzarExcepcionSiFaltanMenosDe24Horas() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("cliente@email.com");

        LocalDateTime fechaInicio = LocalDateTime.now().plusHours(5);

        Reserva reserva = new Reserva();
        reserva.setIdReserva(1L);
        reserva.setUsuario(usuario);
        reserva.setFechaUso(fechaInicio.toLocalDate());
        reserva.setHoraInicio(fechaInicio.toLocalTime());
        reserva.setHoraFin(fechaInicio.plusHours(1).toLocalTime());

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> reservaService.cancelarReserva(1L, "cliente@email.com")
        );

        assertEquals("Solo puedes cancelar con al menos 24 horas de antelación", exception.getMessage());

        verify(reservaRepository, times(1)).findById(1L);
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void obtenerHistorial_debeRetornarReservasDelUsuario() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("cliente@email.com");

        Cancha cancha = new Cancha();
        cancha.setIdCancha(1L);

        Reserva reserva = new Reserva();
        reserva.setIdReserva(1L);
        reserva.setUsuario(usuario);
        reserva.setCancha(cancha);

        when(usuarioRepository.findByEmail("cliente@email.com"))
                .thenReturn(Optional.of(usuario));

        when(reservaRepository.findByUsuarioId(1L))
                .thenReturn(List.of(reserva));

        List<Reserva> historial = reservaService.obtenerHistorial("cliente@email.com");

        assertNotNull(historial);
        assertEquals(1, historial.size());
        assertEquals(1L, historial.get(0).getIdReserva());

        verify(usuarioRepository, times(1)).findByEmail("cliente@email.com");
        verify(reservaRepository, times(1)).findByUsuarioId(1L);
    }

    @Test
    void obtenerHistorial_debeLanzarExcepcionSiUsuarioNoExiste() {
        when(usuarioRepository.findByEmail("noexiste@email.com"))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> reservaService.obtenerHistorial("noexiste@email.com")
        );

        assertEquals("Usuario no encontrado", exception.getMessage());

        verify(usuarioRepository, times(1)).findByEmail("noexiste@email.com");
        verify(reservaRepository, never()).findByUsuarioId(anyLong());
    }
}
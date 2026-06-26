package com.example.searchsport.service;

import com.example.searchsport.dto.ReservaRequest;
import com.example.searchsport.entity.Cancha;
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
        Usuario usuario = crearUsuario("cliente@email.com");
        ReservaRequest request = crearRequest(LocalTime.of(20, 0), LocalTime.of(21, 0));

        when(usuarioRepository.findByEmail("cliente@email.com")).thenReturn(Optional.of(usuario));
        when(reservaRepository.findByCanchaIdCanchaAndFechaUso(1L, LocalDate.of(2026, 7, 10)))
                .thenReturn(List.of());
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Reserva reservaCreada = reservaService.crearReserva(request, "cliente@email.com");

        assertNotNull(reservaCreada);
        assertEquals(LocalDate.of(2026, 7, 10), reservaCreada.getFechaUso());
        assertEquals(LocalTime.of(20, 0), reservaCreada.getHoraInicio());
        assertEquals(LocalTime.of(21, 0), reservaCreada.getHoraFin());
        assertEquals(new BigDecimal("30000"), reservaCreada.getMontoTotal());
        assertEquals(1L, reservaCreada.getCancha().getIdCancha());
        assertEquals("cliente@email.com", reservaCreada.getUsuario().getEmail());
        assertEquals(1L, reservaCreada.getEstado().getIdEstado());

        verify(reservaRepository).save(any(Reserva.class));
    }

    @Test
    void crearReserva_debeCrearReservaSiHoraInicioEsIgualAHoraFinDeReservaExistente() {
        Usuario usuario = crearUsuario("cliente@email.com");
        ReservaRequest request = crearRequest(LocalTime.of(21, 0), LocalTime.of(22, 0));
        Reserva reservaExistente = crearReservaExistente(LocalTime.of(20, 0), LocalTime.of(21, 0));

        when(usuarioRepository.findByEmail("cliente@email.com")).thenReturn(Optional.of(usuario));
        when(reservaRepository.findByCanchaIdCanchaAndFechaUso(1L, LocalDate.of(2026, 7, 10)))
                .thenReturn(List.of(reservaExistente));
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Reserva reservaCreada = reservaService.crearReserva(request, "cliente@email.com");

        assertNotNull(reservaCreada);
        assertEquals(LocalTime.of(21, 0), reservaCreada.getHoraInicio());
        assertEquals(LocalTime.of(22, 0), reservaCreada.getHoraFin());

        verify(reservaRepository).save(any(Reserva.class));
    }

    @Test
    void crearReserva_debeCrearReservaSiHoraInicioEsAntesDeReservaExistente() {
        Usuario usuario = crearUsuario("cliente@email.com");
        ReservaRequest request = crearRequest(LocalTime.of(19, 0), LocalTime.of(20, 0));
        Reserva reservaExistente = crearReservaExistente(LocalTime.of(20, 0), LocalTime.of(21, 0));

        when(usuarioRepository.findByEmail("cliente@email.com")).thenReturn(Optional.of(usuario));
        when(reservaRepository.findByCanchaIdCanchaAndFechaUso(1L, LocalDate.of(2026, 7, 10)))
                .thenReturn(List.of(reservaExistente));
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Reserva reservaCreada = reservaService.crearReserva(request, "cliente@email.com");

        assertNotNull(reservaCreada);
        assertEquals(LocalTime.of(19, 0), reservaCreada.getHoraInicio());
        assertEquals(LocalTime.of(20, 0), reservaCreada.getHoraFin());

        verify(reservaRepository).save(any(Reserva.class));
    }

    @Test
    void crearReserva_debeLanzarExcepcionSiUsuarioNoExiste() {
        ReservaRequest request = crearRequest(LocalTime.of(20, 0), LocalTime.of(21, 0));

        when(usuarioRepository.findByEmail("noexiste@email.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> reservaService.crearReserva(request, "noexiste@email.com")
        );

        assertEquals("Usuario no encontrado en la base de datos", exception.getMessage());
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void crearReserva_debeLanzarExcepcionSiHorarioTieneMismaHoraDeInicio() {
        Usuario usuario = crearUsuario("cliente@email.com");
        ReservaRequest request = crearRequest(LocalTime.of(20, 0), LocalTime.of(21, 0));
        Reserva reservaExistente = crearReservaExistente(LocalTime.of(20, 0), LocalTime.of(21, 0));

        when(usuarioRepository.findByEmail("cliente@email.com")).thenReturn(Optional.of(usuario));
        when(reservaRepository.findByCanchaIdCanchaAndFechaUso(1L, LocalDate.of(2026, 7, 10)))
                .thenReturn(List.of(reservaExistente));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> reservaService.crearReserva(request, "cliente@email.com")
        );

        assertEquals("Lo sentimos, este bloque horario acaba de ser reservado por alguien más.", exception.getMessage());
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void crearReserva_debeLanzarExcepcionSiHoraInicioEstaDentroDeOtraReserva() {
        Usuario usuario = crearUsuario("cliente@email.com");
        ReservaRequest request = crearRequest(LocalTime.of(20, 30), LocalTime.of(21, 30));
        Reserva reservaExistente = crearReservaExistente(LocalTime.of(20, 0), LocalTime.of(21, 0));

        when(usuarioRepository.findByEmail("cliente@email.com")).thenReturn(Optional.of(usuario));
        when(reservaRepository.findByCanchaIdCanchaAndFechaUso(1L, LocalDate.of(2026, 7, 10)))
                .thenReturn(List.of(reservaExistente));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> reservaService.crearReserva(request, "cliente@email.com")
        );

        assertEquals("Lo sentimos, este bloque horario acaba de ser reservado por alguien más.", exception.getMessage());
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void confirmarPago_debeCambiarEstadoAPagada() {
        Usuario usuario = crearUsuario("cliente@email.com");
        Reserva reserva = new Reserva();
        reserva.setIdReserva(1L);
        reserva.setUsuario(usuario);

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(reservaRepository.save(reserva)).thenReturn(reserva);

        Reserva reservaPagada = reservaService.confirmarPago(1L, "cliente@email.com");

        assertNotNull(reservaPagada);
        assertEquals(2L, reservaPagada.getEstado().getIdEstado());
        verify(reservaRepository).save(reserva);
    }

    @Test
    void confirmarPago_debeLanzarExcepcionSiReservaNoExiste() {
        when(reservaRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> reservaService.confirmarPago(99L, "cliente@email.com")
        );

        assertEquals("Reserva no encontrada", exception.getMessage());
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void confirmarPago_debeLanzarExcepcionSiUsuarioNoEsDuenioDeLaReserva() {
        Usuario usuario = crearUsuario("otro@email.com");
        Reserva reserva = new Reserva();
        reserva.setIdReserva(1L);
        reserva.setUsuario(usuario);

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> reservaService.confirmarPago(1L, "cliente@email.com")
        );

        assertEquals("No tienes permiso para pagar esta reserva", exception.getMessage());
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void cancelarReserva_debeCambiarEstadoACanceladaSiTieneMasDe24Horas() {
        Usuario usuario = crearUsuario("cliente@email.com");
        LocalDateTime fechaInicio = LocalDateTime.now().plusDays(3);

        Reserva reserva = crearReservaConUsuarioYFecha(usuario, fechaInicio);

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(reservaRepository.save(reserva)).thenReturn(reserva);

        Reserva reservaCancelada = reservaService.cancelarReserva(1L, "cliente@email.com");

        assertNotNull(reservaCancelada);
        assertEquals(3L, reservaCancelada.getEstado().getIdEstado());
        verify(reservaRepository).save(reserva);
    }

    @Test
    void cancelarReserva_debeLanzarExcepcionSiReservaNoExiste() {
        when(reservaRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> reservaService.cancelarReserva(99L, "cliente@email.com")
        );

        assertEquals("Reserva no encontrada", exception.getMessage());
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void cancelarReserva_debeLanzarExcepcionSiUsuarioNoEsDuenioDeLaReserva() {
        Usuario usuario = crearUsuario("otro@email.com");
        LocalDateTime fechaInicio = LocalDateTime.now().plusDays(3);
        Reserva reserva = crearReservaConUsuarioYFecha(usuario, fechaInicio);

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> reservaService.cancelarReserva(1L, "cliente@email.com")
        );

        assertEquals("No tienes permiso para cancelar esta reserva", exception.getMessage());
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void cancelarReserva_debeLanzarExcepcionSiFaltanMenosDe24Horas() {
        Usuario usuario = crearUsuario("cliente@email.com");
        LocalDateTime fechaInicio = LocalDateTime.now().plusHours(5);
        Reserva reserva = crearReservaConUsuarioYFecha(usuario, fechaInicio);

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> reservaService.cancelarReserva(1L, "cliente@email.com")
        );

        assertEquals("Solo puedes cancelar con al menos 24 horas de antelación", exception.getMessage());
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void obtenerHistorial_debeRetornarReservasDelUsuario() {
        Usuario usuario = crearUsuario("cliente@email.com");
        Cancha cancha = new Cancha();
        cancha.setIdCancha(1L);

        Reserva reserva = new Reserva();
        reserva.setIdReserva(1L);
        reserva.setUsuario(usuario);
        reserva.setCancha(cancha);

        when(usuarioRepository.findByEmail("cliente@email.com")).thenReturn(Optional.of(usuario));
        when(reservaRepository.findByUsuarioId(1L)).thenReturn(List.of(reserva));

        List<Reserva> historial = reservaService.obtenerHistorial("cliente@email.com");

        assertNotNull(historial);
        assertEquals(1, historial.size());
        assertEquals(1L, historial.get(0).getIdReserva());
    }

    @Test
    void obtenerHistorial_debeLanzarExcepcionSiUsuarioNoExiste() {
        when(usuarioRepository.findByEmail("noexiste@email.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> reservaService.obtenerHistorial("noexiste@email.com")
        );

        assertEquals("Usuario no encontrado", exception.getMessage());
        verify(reservaRepository, never()).findByUsuarioId(anyLong());
    }

    private Usuario crearUsuario(String email) {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail(email);
        return usuario;
    }

    private ReservaRequest crearRequest(LocalTime horaInicio, LocalTime horaFin) {
        ReservaRequest request = new ReservaRequest();
        request.setCanchaId(1L);
        request.setFechaUso(LocalDate.of(2026, 7, 10));
        request.setHoraInicio(horaInicio);
        request.setHoraFin(horaFin);
        request.setMontoTotal(new BigDecimal("30000"));
        return request;
    }

    private Reserva crearReservaExistente(LocalTime horaInicio, LocalTime horaFin) {
        Reserva reserva = new Reserva();
        reserva.setHoraInicio(horaInicio);
        reserva.setHoraFin(horaFin);
        return reserva;
    }

    private Reserva crearReservaConUsuarioYFecha(Usuario usuario, LocalDateTime fechaInicio) {
        Reserva reserva = new Reserva();
        reserva.setIdReserva(1L);
        reserva.setUsuario(usuario);
        reserva.setFechaUso(fechaInicio.toLocalDate());
        reserva.setHoraInicio(fechaInicio.toLocalTime());
        reserva.setHoraFin(fechaInicio.plusHours(1).toLocalTime());
        return reserva;
    }
}
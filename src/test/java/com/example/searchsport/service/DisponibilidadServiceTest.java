package com.example.searchsport.service;

import com.example.searchsport.dto.BloqueDisponibleDTO;
import com.example.searchsport.entity.EstadoReserva;
import com.example.searchsport.entity.HorarioEspecial;
import com.example.searchsport.entity.Reserva;
import com.example.searchsport.repository.HorarioEspecialRepository;
import com.example.searchsport.repository.ReservaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DisponibilidadServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private HorarioEspecialRepository horarioEspecialRepository;

    @Mock
    private TarifaService tarifaService;

    @InjectMocks
    private DisponibilidadService disponibilidadService;

    @Test
    void obtenerBloquesDisponibles_debeRetornarListaVaciaSiCanchaIdEsNull() {
        List<BloqueDisponibleDTO> bloques =
                disponibilidadService.obtenerBloquesDisponibles(null, LocalDate.of(2026, 7, 10));

        assertNotNull(bloques);
        assertTrue(bloques.isEmpty());

        verifyNoInteractions(horarioEspecialRepository);
        verifyNoInteractions(reservaRepository);
        verifyNoInteractions(tarifaService);
    }

    @Test
    void obtenerBloquesDisponibles_debeRetornarListaVaciaSiFechaEsNull() {
        List<BloqueDisponibleDTO> bloques =
                disponibilidadService.obtenerBloquesDisponibles(1L, null);

        assertNotNull(bloques);
        assertTrue(bloques.isEmpty());

        verifyNoInteractions(horarioEspecialRepository);
        verifyNoInteractions(reservaRepository);
        verifyNoInteractions(tarifaService);
    }

    @Test
    void obtenerBloquesDisponibles_debeRetornarListaVaciaSiDiaCompletoEstaBloqueado() {
        Long canchaId = 1L;
        LocalDate fecha = LocalDate.of(2026, 7, 10);

        HorarioEspecial bloqueo = new HorarioEspecial();
        bloqueo.setEstaBloqueado(true);

        when(horarioEspecialRepository.findByCanchaIdCanchaAndFecha(canchaId, fecha))
                .thenReturn(List.of(bloqueo));

        List<BloqueDisponibleDTO> bloques =
                disponibilidadService.obtenerBloquesDisponibles(canchaId, fecha);

        assertNotNull(bloques);
        assertTrue(bloques.isEmpty());

        verify(horarioEspecialRepository, times(1))
                .findByCanchaIdCanchaAndFecha(canchaId, fecha);
        verifyNoInteractions(reservaRepository);
        verifyNoInteractions(tarifaService);
    }

    @Test
    void obtenerBloquesDisponibles_debeRetornarTodosLosBloquesSiNoHayReservasNiBloqueos() {
        Long canchaId = 1L;
        LocalDate fecha = LocalDate.of(2026, 7, 10);
        int diaSemana = fecha.getDayOfWeek().getValue();

        when(horarioEspecialRepository.findByCanchaIdCanchaAndFecha(canchaId, fecha))
                .thenReturn(List.of());

        when(reservaRepository.findByCanchaIdCanchaAndFechaUso(canchaId, fecha))
                .thenReturn(List.of());

        when(tarifaService.calcularPrecio(eq(canchaId), eq(diaSemana), any(LocalTime.class)))
                .thenReturn(new BigDecimal("30000"));

        List<BloqueDisponibleDTO> bloques =
                disponibilidadService.obtenerBloquesDisponibles(canchaId, fecha);

        assertNotNull(bloques);
        assertEquals(14, bloques.size());

        assertEquals(LocalTime.of(9, 0), bloques.get(0).getHoraInicio());
        assertEquals(LocalTime.of(10, 0), bloques.get(0).getHoraFin());
        assertEquals(new BigDecimal("30000"), bloques.get(0).getPrecio());

        assertEquals(LocalTime.of(22, 0), bloques.get(13).getHoraInicio());
        assertEquals(LocalTime.of(23, 0), bloques.get(13).getHoraFin());

        verify(horarioEspecialRepository, times(1))
                .findByCanchaIdCanchaAndFecha(canchaId, fecha);
        verify(reservaRepository, times(1))
                .findByCanchaIdCanchaAndFechaUso(canchaId, fecha);
        verify(tarifaService, times(14))
                .calcularPrecio(eq(canchaId), eq(diaSemana), any(LocalTime.class));
    }

    @Test
    void obtenerBloquesDisponibles_debeExcluirBloquesReservados() {
        Long canchaId = 1L;
        LocalDate fecha = LocalDate.of(2026, 7, 10);
        int diaSemana = fecha.getDayOfWeek().getValue();

        Reserva reservaExistente = new Reserva();
        reservaExistente.setHoraInicio(LocalTime.of(10, 0));
        reservaExistente.setHoraFin(LocalTime.of(12, 0));

        when(horarioEspecialRepository.findByCanchaIdCanchaAndFecha(canchaId, fecha))
                .thenReturn(List.of());

        when(reservaRepository.findByCanchaIdCanchaAndFechaUso(canchaId, fecha))
                .thenReturn(List.of(reservaExistente));

        when(tarifaService.calcularPrecio(eq(canchaId), eq(diaSemana), any(LocalTime.class)))
                .thenReturn(new BigDecimal("30000"));

        List<BloqueDisponibleDTO> bloques =
                disponibilidadService.obtenerBloquesDisponibles(canchaId, fecha);

        assertNotNull(bloques);
        assertEquals(12, bloques.size());

        boolean contieneBloque10 = bloques.stream()
                .anyMatch(bloque -> bloque.getHoraInicio().equals(LocalTime.of(10, 0)));

        boolean contieneBloque11 = bloques.stream()
                .anyMatch(bloque -> bloque.getHoraInicio().equals(LocalTime.of(11, 0)));

        boolean contieneBloque9 = bloques.stream()
                .anyMatch(bloque -> bloque.getHoraInicio().equals(LocalTime.of(9, 0)));

        boolean contieneBloque12 = bloques.stream()
                .anyMatch(bloque -> bloque.getHoraInicio().equals(LocalTime.of(12, 0)));

        assertFalse(contieneBloque10);
        assertFalse(contieneBloque11);
        assertTrue(contieneBloque9);
        assertTrue(contieneBloque12);

        verify(horarioEspecialRepository, times(1))
                .findByCanchaIdCanchaAndFecha(canchaId, fecha);
        verify(reservaRepository, times(1))
                .findByCanchaIdCanchaAndFechaUso(canchaId, fecha);
        verify(tarifaService, times(12))
                .calcularPrecio(eq(canchaId), eq(diaSemana), any(LocalTime.class));
    }

    @Test
    void obtenerBloquesDisponibles_debeExcluirBloqueCuandoHoraInicioCoincideConReserva() {
        Long canchaId = 1L;
        LocalDate fecha = LocalDate.of(2026, 7, 10);
        int diaSemana = fecha.getDayOfWeek().getValue();

        Reserva reservaExistente = new Reserva();
        reservaExistente.setHoraInicio(LocalTime.of(15, 0));
        reservaExistente.setHoraFin(LocalTime.of(16, 0));

        when(horarioEspecialRepository.findByCanchaIdCanchaAndFecha(canchaId, fecha))
                .thenReturn(List.of());

        when(reservaRepository.findByCanchaIdCanchaAndFechaUso(canchaId, fecha))
                .thenReturn(List.of(reservaExistente));

        when(tarifaService.calcularPrecio(eq(canchaId), eq(diaSemana), any(LocalTime.class)))
                .thenReturn(new BigDecimal("30000"));

        List<BloqueDisponibleDTO> bloques =
                disponibilidadService.obtenerBloquesDisponibles(canchaId, fecha);

        assertNotNull(bloques);
        assertEquals(13, bloques.size());

        boolean contieneBloque15 = bloques.stream()
                .anyMatch(bloque -> bloque.getHoraInicio().equals(LocalTime.of(15, 0)));

        assertFalse(contieneBloque15);

        verify(horarioEspecialRepository, times(1))
                .findByCanchaIdCanchaAndFecha(canchaId, fecha);
        verify(reservaRepository, times(1))
                .findByCanchaIdCanchaAndFechaUso(canchaId, fecha);
        verify(tarifaService, times(13))
                .calcularPrecio(eq(canchaId), eq(diaSemana), any(LocalTime.class));
    }

    @Test
    void obtenerBloquesDisponibles_noDebeExcluirBloqueSiReservaEstaCancelada() {
        Long canchaId = 1L;
        LocalDate fecha = LocalDate.of(2026, 7, 10);
        int diaSemana = fecha.getDayOfWeek().getValue();

        EstadoReserva estadoCancelado = new EstadoReserva();
        estadoCancelado.setDescripcion("CANCELADA");

        Reserva reservaCancelada = new Reserva();
        reservaCancelada.setHoraInicio(LocalTime.of(15, 0));
        reservaCancelada.setHoraFin(LocalTime.of(16, 0));
        reservaCancelada.setEstado(estadoCancelado);

        when(horarioEspecialRepository.findByCanchaIdCanchaAndFecha(canchaId, fecha))
                .thenReturn(List.of());

        when(reservaRepository.findByCanchaIdCanchaAndFechaUso(canchaId, fecha))
                .thenReturn(List.of(reservaCancelada));

        when(tarifaService.calcularPrecio(eq(canchaId), eq(diaSemana), any(LocalTime.class)))
                .thenReturn(new BigDecimal("30000"));

        List<BloqueDisponibleDTO> bloques =
                disponibilidadService.obtenerBloquesDisponibles(canchaId, fecha);

        assertNotNull(bloques);
        assertEquals(14, bloques.size());

        boolean contieneBloque15 = bloques.stream()
                .anyMatch(bloque -> bloque.getHoraInicio().equals(LocalTime.of(15, 0)));

        assertTrue(contieneBloque15);

        verify(horarioEspecialRepository, times(1))
                .findByCanchaIdCanchaAndFecha(canchaId, fecha);
        verify(reservaRepository, times(1))
                .findByCanchaIdCanchaAndFechaUso(canchaId, fecha);
        verify(tarifaService, times(14))
                .calcularPrecio(eq(canchaId), eq(diaSemana), any(LocalTime.class));
    }
}
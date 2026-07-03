package com.example.searchsport.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.searchsport.dto.BloqueDisponibleDTO;
import com.example.searchsport.entity.HorarioEspecial;
import com.example.searchsport.entity.Reserva;
import com.example.searchsport.repository.HorarioEspecialRepository;
import com.example.searchsport.repository.ReservaRepository;

@Service
public class DisponibilidadService {

    private static final Long ESTADO_PENDIENTE_ID = 1L;
    private static final Long ESTADO_PAGADA_ID = 2L;
    private static final Long ESTADO_CANCELADA_ID = 3L;

    private static final LocalTime APERTURA = LocalTime.of(9, 0);
    private static final LocalTime CIERRE = LocalTime.of(23, 0);

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private HorarioEspecialRepository horarioEspecialRepository;

    @Autowired
    private TarifaService tarifaService;

    public List<BloqueDisponibleDTO> obtenerBloquesDisponibles(Long canchaId, LocalDate fecha) {
        List<BloqueDisponibleDTO> bloquesLibres = new ArrayList<>();

        if (canchaId == null || fecha == null) {
            return bloquesLibres;
        }

        LocalDate hoy = LocalDate.now();

        if (fecha.isBefore(hoy)) {
            return bloquesLibres;
        }

        List<HorarioEspecial> bloqueos =
                horarioEspecialRepository.findByCanchaIdCanchaAndFecha(canchaId, fecha);

        boolean diaCompletoBloqueado = bloqueos.stream()
                .anyMatch(bloqueo -> Boolean.TRUE.equals(bloqueo.getEstaBloqueado()));

        if (diaCompletoBloqueado) {
            return bloquesLibres;
        }

        List<Reserva> reservasDelDia =
                reservaRepository.findByCanchaIdCanchaAndFechaUso(canchaId, fecha)
                        .stream()
                        .filter(this::debeBloquearHorario)
                        .toList();

        LocalTime horaActual = APERTURA;
        int diaSemana = fecha.getDayOfWeek().getValue();

        while (horaActual.isBefore(CIERRE)) {
            LocalTime horaFinBloque = horaActual.plusHours(1);

            final LocalTime horaInicioBloque = horaActual;
            final LocalTime horaFinActual = horaFinBloque;

            boolean bloqueYaPaso = bloqueYaPaso(fecha, horaFinActual);

            boolean estaReservado = reservasDelDia.stream()
                    .anyMatch(reserva -> seCruzanHorarios(
                            horaInicioBloque,
                            horaFinActual,
                            reserva.getHoraInicio(),
                            reserva.getHoraFin()
                    ));

            if (!bloqueYaPaso && !estaReservado) {
                BigDecimal precio = tarifaService.calcularPrecio(
                        canchaId,
                        diaSemana,
                        horaInicioBloque
                );

                bloquesLibres.add(
                        new BloqueDisponibleDTO(
                                horaInicioBloque,
                                horaFinBloque,
                                precio
                        )
                );
            }

            horaActual = horaActual.plusHours(1);
        }

        return bloquesLibres;
    }

    private boolean bloqueYaPaso(LocalDate fecha, LocalTime horaFinBloque) {
        LocalDate hoy = LocalDate.now();

        if (!fecha.isEqual(hoy)) {
            return false;
        }

        LocalDateTime finBloque = LocalDateTime.of(fecha, horaFinBloque);

        return !finBloque.isAfter(LocalDateTime.now());
    }

    private boolean seCruzanHorarios(
            LocalTime inicioBloque,
            LocalTime finBloque,
            LocalTime inicioReserva,
            LocalTime finReserva
    ) {
        if (inicioReserva == null || finReserva == null) {
            return false;
        }

        return inicioBloque.isBefore(finReserva) && finBloque.isAfter(inicioReserva);
    }

    private boolean debeBloquearHorario(Reserva reserva) {
        if (reserva == null) {
            return true;
        }

        if (reserva.getEstado() == null) {
            return true;
        }

        Long estadoId = reserva.getEstado().getIdEstado();

        if (Objects.equals(estadoId, ESTADO_CANCELADA_ID)) {
            return false;
        }

        if (Objects.equals(estadoId, ESTADO_PENDIENTE_ID)
                || Objects.equals(estadoId, ESTADO_PAGADA_ID)) {
            return true;
        }

        String descripcion = reserva.getEstado().getDescripcion();

        if (descripcion == null || descripcion.trim().isBlank()) {
            return true;
        }

        String estadoNormalizado = descripcion.toLowerCase();

        if (estadoNormalizado.contains("cancel")) {
            return false;
        }

        if (estadoNormalizado.contains("pend")) {
            return true;
        }

        if (estadoNormalizado.contains("pag")) {
            return true;
        }

        return true;
    }
}
package com.example.searchsport.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.searchsport.dto.BloqueDisponibleDTO;
import com.example.searchsport.entity.HorarioEspecial;
import com.example.searchsport.entity.Reserva;
import com.example.searchsport.repository.HorarioEspecialRepository;
import com.example.searchsport.repository.ReservaRepository;

@Service
public class DisponibilidadService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private HorarioEspecialRepository horarioEspecialRepository;

    @Autowired
    private TarifaService tarifaService;

    private static final LocalTime APERTURA = LocalTime.of(9, 0);
    private static final LocalTime CIERRE = LocalTime.of(23, 0);

    public List<BloqueDisponibleDTO> obtenerBloquesDisponibles(Long canchaId, LocalDate fecha) {
        List<BloqueDisponibleDTO> bloquesLibres = new ArrayList<>();

        if (canchaId == null || fecha == null) {
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

            boolean estaReservado = reservasDelDia.stream()
                    .anyMatch(reserva -> seCruzanHorarios(
                            horaInicioBloque,
                            horaFinActual,
                            reserva.getHoraInicio(),
                            reserva.getHoraFin()
                    ));

            if (!estaReservado) {
                BigDecimal precio = tarifaService.calcularPrecio(canchaId, diaSemana, horaInicioBloque);
                bloquesLibres.add(new BloqueDisponibleDTO(horaInicioBloque, horaFinBloque, precio));
            }

            horaActual = horaActual.plusHours(1);
        }

        return bloquesLibres;
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
        if (reserva == null || reserva.getEstado() == null) {
            return true;
        }

        String estado = reserva.getEstado().getDescripcion();

        if (estado == null) {
            return true;
        }

        String estadoNormalizado = estado.toLowerCase();

        return !estadoNormalizado.contains("cancel");
    }
}
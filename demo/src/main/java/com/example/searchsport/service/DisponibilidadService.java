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

        List<HorarioEspecial> bloqueos = horarioEspecialRepository.findByCanchaIdCanchaAndFecha(canchaId, fecha);
        boolean diaCompletoBloqueado = bloqueos.stream()
                .anyMatch(bloqueo -> Boolean.TRUE.equals(bloqueo.getEstaBloqueado()));

        if (diaCompletoBloqueado) {
            return bloquesLibres;
        }

        List<Reserva> reservasDelDia = reservaRepository.findByCanchaIdCanchaAndFechaUso(canchaId, fecha);

        LocalTime horaActual = APERTURA;
        int diaSemana = fecha.getDayOfWeek().getValue();

        while (horaActual.isBefore(CIERRE)) {
            LocalTime horaFinBloque = horaActual.plusHours(1);
            final LocalTime horaIteracion = horaActual;

            boolean estaReservado = reservasDelDia.stream().anyMatch(reserva ->
                    horaIteracion.equals(reserva.getHoraInicio()) ||
                            (horaIteracion.isAfter(reserva.getHoraInicio()) && horaIteracion.isBefore(reserva.getHoraFin()))
            );

            if (!estaReservado) {
                BigDecimal precio = tarifaService.calcularPrecio(canchaId, diaSemana, horaIteracion);
                bloquesLibres.add(new BloqueDisponibleDTO(horaIteracion, horaFinBloque, precio));
            }

            horaActual = horaActual.plusHours(1);
        }

        return bloquesLibres;
    }
}
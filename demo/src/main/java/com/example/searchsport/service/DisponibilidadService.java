package com.example.searchsport.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.searchsport.dto.BloqueDisponibleDTO;
import com.example.searchsport.entity.HorarioEspecial;
import com.example.searchsport.entity.Reserva;
import com.example.searchsport.repository.HorarioEspecialRepository;
import com.example.searchsport.repository.ReservaRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DisponibilidadService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private HorarioEspecialRepository horarioEspecialRepository;

    @Autowired
    private TarifaService tarifaService;

    // Horario de operación del recinto (Se podría mover a la base de datos en el futuro)
    private static final LocalTime APERTURA = LocalTime.of(9, 0);
    private static final LocalTime CIERRE = LocalTime.of(23, 0);

    public List<BloqueDisponibleDTO> obtenerBloquesDisponibles(Long canchaId, LocalDate fecha) {
        List<BloqueDisponibleDTO> bloquesLibres = new ArrayList<>();

        // 1. Verificar si hay un bloqueo total por mantenimiento ese día
        List<HorarioEspecial> bloqueos = horarioEspecialRepository.findByCanchaIdCanchaAndFecha(canchaId, fecha);
        boolean diaCompletoBloqueado = bloqueos.stream().anyMatch(HorarioEspecial::getEstaBloqueado);
        if (diaCompletoBloqueado) {
            return bloquesLibres; // Retorna lista vacía, no hay nada disponible
        }

        // 2. Obtener las reservas ya hechas para ese día
        List<Reserva> reservasDelDia = reservaRepository.findByCanchaIdCanchaAndFechaUso(canchaId, fecha);

        // 3. Generar bloques de 1 hora e ir filtrando
        LocalTime horaActual = APERTURA;
        int diaSemana = fecha.getDayOfWeek().getValue(); // 1 = Lunes, 7 = Domingo

        while (horaActual.isBefore(CIERRE)) {
            LocalTime horaFinBloque = horaActual.plusHours(1);
            final LocalTime horaIteracion = horaActual;

            // Revisamos si alguna reserva choca con este bloque
            boolean estaReservado = reservasDelDia.stream().anyMatch(reserva -> 
                (horaIteracion.equals(reserva.getHoraInicio())) || 
                (horaIteracion.isAfter(reserva.getHoraInicio()) && horaIteracion.isBefore(reserva.getHoraFin()))
            );

            // Si no está reservado, calculamos su precio y lo agregamos a los disponibles
            if (!estaReservado) {
                BigDecimal precio = tarifaService.calcularPrecio(canchaId, diaSemana, horaIteracion);
                bloquesLibres.add(new BloqueDisponibleDTO(horaIteracion, horaFinBloque, precio));
            }

            // Avanzamos a la siguiente hora
            horaActual = horaActual.plusHours(1);
        }

        return bloquesLibres;
    }
}
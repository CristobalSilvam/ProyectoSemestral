package com.example.searchsport.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.searchsport.dto.ReservaRequest;
import com.example.searchsport.entity.Cancha;
import com.example.searchsport.entity.EstadoReserva;
import com.example.searchsport.entity.Reserva;
import com.example.searchsport.entity.Usuario;
import com.example.searchsport.repository.ReservaRepository;
import com.example.searchsport.repository.UsuarioRepository;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public Reserva crearReserva(ReservaRequest request, String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado en la base de datos"));

        List<Reserva> reservasExistentes = reservaRepository.findByCanchaIdCanchaAndFechaUso(
                request.getCanchaId(),
                request.getFechaUso()
        );

        boolean conflicto = reservasExistentes.stream().anyMatch(reserva ->
                request.getHoraInicio().equals(reserva.getHoraInicio()) ||
                        (request.getHoraInicio().isAfter(reserva.getHoraInicio())
                                && request.getHoraInicio().isBefore(reserva.getHoraFin()))
        );

        if (conflicto) {
            throw new RuntimeException("Lo sentimos, este bloque horario acaba de ser reservado por alguien más.");
        }

        Reserva nuevaReserva = new Reserva();
        nuevaReserva.setFechaUso(request.getFechaUso());
        nuevaReserva.setHoraInicio(request.getHoraInicio());
        nuevaReserva.setHoraFin(request.getHoraFin());
        nuevaReserva.setMontoTotal(request.getMontoTotal());

        Cancha cancha = new Cancha();
        cancha.setIdCancha(request.getCanchaId());
        nuevaReserva.setCancha(cancha);

        nuevaReserva.setUsuario(usuario);

        EstadoReserva estado = new EstadoReserva();
        estado.setIdEstado(1L);
        nuevaReserva.setEstado(estado);

        return reservaRepository.save(nuevaReserva);
    }

    @Transactional
    public Reserva confirmarPago(Long idReserva, String emailUsuario) {
        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        if (!reserva.getUsuario().getEmail().equals(emailUsuario)) {
            throw new RuntimeException("No tienes permiso para pagar esta reserva");
        }

        EstadoReserva pagada = new EstadoReserva();
        pagada.setIdEstado(2L);
        reserva.setEstado(pagada);

        return reservaRepository.save(reserva);
    }

    @Transactional
    public Reserva cancelarReserva(Long idReserva, String emailUsuario) {
        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        if (!reserva.getUsuario().getEmail().equals(emailUsuario)) {
            throw new RuntimeException("No tienes permiso para cancelar esta reserva");
        }

        LocalDateTime inicioReserva = LocalDateTime.of(reserva.getFechaUso(), reserva.getHoraInicio());
        if (LocalDateTime.now().isAfter(inicioReserva.minusHours(24))) {
            throw new RuntimeException("Solo puedes cancelar con al menos 24 horas de antelación");
        }

        EstadoReserva cancelada = new EstadoReserva();
        cancelada.setIdEstado(3L);
        reserva.setEstado(cancelada);

        return reservaRepository.save(reserva);
    }

    public List<Reserva> obtenerHistorial(String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return reservaRepository.findByUsuarioId(usuario.getId());
    }
}
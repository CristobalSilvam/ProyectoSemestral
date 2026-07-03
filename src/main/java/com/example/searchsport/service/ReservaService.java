package com.example.searchsport.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

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

    private static final Long ESTADO_PENDIENTE_ID = 1L;
    private static final Long ESTADO_PAGADA_ID = 2L;
    private static final Long ESTADO_CANCELADA_ID = 3L;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public Reserva crearReserva(ReservaRequest request, String emailUsuario) {
        if (request == null) {
            throw new RuntimeException("La solicitud de reserva no puede estar vacía");
        }

        if (request.getCanchaId() == null) {
            throw new RuntimeException("Debes seleccionar una cancha");
        }

        if (request.getFechaUso() == null) {
            throw new RuntimeException("Debes seleccionar una fecha");
        }

        if (request.getHoraInicio() == null || request.getHoraFin() == null) {
            throw new RuntimeException("Debes seleccionar un horario válido");
        }

        if (!request.getHoraFin().isAfter(request.getHoraInicio())) {
            throw new RuntimeException("La hora de término debe ser posterior a la hora de inicio");
        }

        LocalDateTime inicioReservaNueva = LocalDateTime.of(
                request.getFechaUso(),
                request.getHoraInicio()
        );

        if (inicioReservaNueva.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("No puedes crear una reserva para un horario que ya pasó");
        }

        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado en la base de datos"));

        List<Reserva> reservasExistentes = reservaRepository.findByCanchaIdCanchaAndFechaUso(
                request.getCanchaId(),
                request.getFechaUso()
        );

        boolean conflicto = reservasExistentes.stream().anyMatch(reserva -> {
            Long estadoId = reserva.getEstado() != null
                    ? reserva.getEstado().getIdEstado()
                    : null;

            if (Objects.equals(estadoId, ESTADO_CANCELADA_ID)) {
                return false;
            }

            return request.getHoraInicio().isBefore(reserva.getHoraFin())
                    && request.getHoraFin().isAfter(reserva.getHoraInicio());
        });

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

        EstadoReserva estadoPendiente = new EstadoReserva();
        estadoPendiente.setIdEstado(ESTADO_PENDIENTE_ID);
        nuevaReserva.setEstado(estadoPendiente);

        return reservaRepository.save(nuevaReserva);
    }

    @Transactional
    public Reserva confirmarPago(Long idReserva, String emailUsuario) {
        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        validarPropietarioReserva(
                reserva,
                emailUsuario,
                "No tienes permiso para pagar esta reserva"
        );

        Long estadoActualId = reserva.getEstado() != null
                ? reserva.getEstado().getIdEstado()
                : null;

        if (Objects.equals(estadoActualId, ESTADO_CANCELADA_ID)) {
            throw new RuntimeException("No puedes pagar una reserva cancelada");
        }

        if (Objects.equals(estadoActualId, ESTADO_PAGADA_ID)) {
            return reserva;
        }

        if (reserva.getFechaUso() != null && reserva.getHoraInicio() != null) {
            LocalDateTime inicioReserva = LocalDateTime.of(
                    reserva.getFechaUso(),
                    reserva.getHoraInicio()
            );

            if (inicioReserva.isBefore(LocalDateTime.now())) {
                throw new RuntimeException("No puedes pagar una reserva cuyo horario ya pasó");
            }
        }

        EstadoReserva estadoPagada = new EstadoReserva();
        estadoPagada.setIdEstado(ESTADO_PAGADA_ID);
        reserva.setEstado(estadoPagada);

        return reservaRepository.save(reserva);
    }

    @Transactional
    public Reserva cancelarReserva(Long idReserva, String emailUsuario) {
        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        validarPropietarioReserva(
                reserva,
                emailUsuario,
                "No tienes permiso para cancelar esta reserva"
        );

        Long estadoActualId = reserva.getEstado() != null
                ? reserva.getEstado().getIdEstado()
                : null;

        if (Objects.equals(estadoActualId, ESTADO_CANCELADA_ID)) {
            return reserva;
        }

        if (reserva.getFechaUso() != null && reserva.getHoraInicio() != null) {
            LocalDateTime inicioReserva = LocalDateTime.of(
                    reserva.getFechaUso(),
                    reserva.getHoraInicio()
            );

            if (LocalDateTime.now().isAfter(inicioReserva.minusHours(24))) {
                throw new RuntimeException("Solo puedes cancelar con al menos 24 horas de antelación");
            }
        }

        EstadoReserva estadoCancelada = new EstadoReserva();
        estadoCancelada.setIdEstado(ESTADO_CANCELADA_ID);
        reserva.setEstado(estadoCancelada);

        return reservaRepository.save(reserva);
    }

    public List<Reserva> obtenerHistorial(String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Reserva> reservas = new ArrayList<>(
                reservaRepository.findByUsuarioId(usuario.getId())
        );

        boolean todasTienenFechaYHora = reservas.stream().allMatch(reserva ->
                reserva.getFechaUso() != null && reserva.getHoraInicio() != null
        );

        if (todasTienenFechaYHora) {
            reservas.sort(
                    Comparator.comparing(
                                    (Reserva reserva) -> LocalDateTime.of(
                                            reserva.getFechaUso(),
                                            reserva.getHoraInicio()
                                    )
                            )
                            .reversed()
            );
        }

        return reservas;
    }

    public Reserva obtenerReservaPorId(Long idReserva, String emailUsuario) {
        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        validarPropietarioReserva(
                reserva,
                emailUsuario,
                "No tienes permiso para ver esta reserva"
        );

        return reserva;
    }

    private void validarPropietarioReserva(
            Reserva reserva,
            String emailUsuario,
            String mensajeError
    ) {
        if (reserva.getUsuario() == null || reserva.getUsuario().getEmail() == null) {
            throw new RuntimeException("La reserva no tiene un usuario válido");
        }

        if (!reserva.getUsuario().getEmail().equals(emailUsuario)) {
            throw new RuntimeException(mensajeError);
        }
    }
}
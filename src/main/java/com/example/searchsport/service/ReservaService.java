package com.example.searchsport.service;

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

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public Reserva crearReserva(ReservaRequest request, String emailUsuario) {
        
        // 1. Buscar al usuario autenticado usando el email extraído del Token
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado en la base de datos"));

        // 2. Control de Concurrencia (Doble chequeo)
        // Verificamos si en la base de datos ya existe una reserva que choque con este horario
        List<Reserva> reservasExistentes = reservaRepository.findByCanchaIdCanchaAndFechaUso(
                request.getCanchaId(), request.getFechaUso());

        boolean conflicto = reservasExistentes.stream().anyMatch(reserva -> 
            (request.getHoraInicio().equals(reserva.getHoraInicio())) || 
            (request.getHoraInicio().isAfter(reserva.getHoraInicio()) && request.getHoraInicio().isBefore(reserva.getHoraFin()))
        );

        if (conflicto) {
            throw new RuntimeException("Lo sentimos, este bloque horario acaba de ser reservado por alguien más.");
        }

        // 3. Crear la Reserva
        Reserva nuevaReserva = new Reserva();
        nuevaReserva.setFechaUso(request.getFechaUso());
        nuevaReserva.setHoraInicio(request.getHoraInicio());
        nuevaReserva.setHoraFin(request.getHoraFin());
        nuevaReserva.setMontoTotal(request.getMontoTotal());

        // Asignar Relaciones
        Cancha cancha = new Cancha();
        cancha.setIdCancha(request.getCanchaId());
        nuevaReserva.setCancha(cancha);

        nuevaReserva.setUsuario(usuario);

        // Asignar Estado: Asumimos que 1 = "Pendiente de Pago"
        EstadoReserva estado = new EstadoReserva();
        estado.setIdEstado(1L); 
        nuevaReserva.setEstado(estado);

        // 4. Guardar en Base de Datos
        return reservaRepository.save(nuevaReserva);
    }
    // Confirmar Pago
    @Transactional
    public Reserva confirmarPago(Long idReserva, String emailUsuario) {
        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        // Seguridad: Validar que la reserva pertenezca al usuario del Token
        if (!reserva.getUsuario().getEmail().equals(emailUsuario)) {
            throw new RuntimeException("No tienes permiso para pagar esta reserva");
        }

        // Cambiar estado a 2 = "Pagada"
        EstadoReserva pagada = new EstadoReserva();
        pagada.setIdEstado(2L);
        reserva.setEstado(pagada);

        return reservaRepository.save(reserva);
    }

    // Cancelación con regla de 24 horas
    @Transactional
    public Reserva cancelarReserva(Long idReserva, String emailUsuario) {
        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        if (!reserva.getUsuario().getEmail().equals(emailUsuario)) {
            throw new RuntimeException("No tienes permiso para cancelar esta reserva");
        }

        // Lógica de negocio: Validar 24 horas de antelación
        LocalDateTime inicioReserva = LocalDateTime.of(reserva.getFechaUso(), reserva.getHoraInicio());
        if (LocalDateTime.now().isAfter(inicioReserva.minusHours(24))) {
            throw new RuntimeException("Solo puedes cancelar con al menos 24 horas de antelación");
        }

        // Cambiar estado a 3 = "Cancelada"
        EstadoReserva cancelada = new EstadoReserva();
        cancelada.setIdEstado(3L);
        reserva.setEstado(cancelada);

        return reservaRepository.save(reserva);
    }

    //  Historial del deportista
    public List<Reserva> obtenerHistorial(String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        return reservaRepository.findByUsuarioId(usuario.getId());
    }
}
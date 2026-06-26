package com.example.searchsport.controller;

import com.example.searchsport.entity.Cancha;
import com.example.searchsport.entity.Deporte;
import com.example.searchsport.entity.EstadoReserva;
import com.example.searchsport.entity.Recinto;
import com.example.searchsport.entity.Reserva;
import com.example.searchsport.entity.Usuario;
import com.example.searchsport.repository.EstadoReservaRepository;
import com.example.searchsport.repository.ReservaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/reservas")
public class AdminReservaController {

    private final ReservaRepository reservaRepository;
    private final EstadoReservaRepository estadoReservaRepository;

    public AdminReservaController(
            ReservaRepository reservaRepository,
            EstadoReservaRepository estadoReservaRepository
    ) {
        this.reservaRepository = reservaRepository;
        this.estadoReservaRepository = estadoReservaRepository;
    }

    @GetMapping
    public ResponseEntity<List<ReservaResponse>> listarReservas() {
        List<ReservaResponse> reservas = reservaRepository.findAll()
                .stream()
                .map(ReservaResponse::new)
                .toList();

        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/estados")
    public ResponseEntity<List<EstadoReservaResponse>> listarEstados() {
        List<EstadoReservaResponse> estados = estadoReservaRepository.findAll()
                .stream()
                .map(EstadoReservaResponse::new)
                .toList();

        return ResponseEntity.ok(estados);
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<ReservaResponse> cambiarEstado(
            @PathVariable Long id,
            @RequestBody CambiarEstadoRequest request
    ) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        EstadoReserva estado = estadoReservaRepository.findById(request.getEstadoId())
                .orElseThrow(() -> new RuntimeException("Estado de reserva no encontrado"));

        reserva.setEstado(estado);

        Reserva actualizada = reservaRepository.save(reserva);

        return ResponseEntity.ok(new ReservaResponse(actualizada));
    }

    public static class CambiarEstadoRequest {
        private Long estadoId;

        public Long getEstadoId() {
            return estadoId;
        }

        public void setEstadoId(Long estadoId) {
            this.estadoId = estadoId;
        }
    }

    public static class EstadoReservaResponse {
        private Long id;
        private String descripcion;

        public EstadoReservaResponse(EstadoReserva estado) {
            this.id = estado.getIdEstado();
            this.descripcion = estado.getDescripcion();
        }

        public Long getId() {
            return id;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }

    public static class ReservaResponse {
        private Long id;
        private LocalDate fechaUso;
        private LocalTime horaInicio;
        private LocalTime horaFin;
        private BigDecimal montoTotal;

        private Long usuarioId;
        private String usuarioNombre;
        private String usuarioEmail;

        private Long canchaId;
        private String canchaNombre;
        private Boolean canchaTechada;
        private String canchaSuperficie;

        private Long recintoId;
        private String recintoNombre;

        private Number deporteId;
        private String deporteNombre;

        private Long estadoId;
        private String estadoDescripcion;

        public ReservaResponse(Reserva reserva) {
            this.id = reserva.getIdReserva();
            this.fechaUso = reserva.getFechaUso();
            this.horaInicio = reserva.getHoraInicio();
            this.horaFin = reserva.getHoraFin();
            this.montoTotal = reserva.getMontoTotal();

            Usuario usuario = reserva.getUsuario();
            if (usuario != null) {
                this.usuarioId = usuario.getId();
                this.usuarioNombre = nombreCompleto(usuario);
                this.usuarioEmail = usuario.getEmail();
            }

            Cancha cancha = reserva.getCancha();
            if (cancha != null) {
                this.canchaId = cancha.getIdCancha();
                this.canchaNombre = cancha.getNombreInterno();
                this.canchaTechada = Boolean.TRUE.equals(cancha.getEsTechada());
                this.canchaSuperficie = cancha.getTipoSuperficie();

                Recinto recinto = cancha.getRecinto();
                if (recinto != null) {
                    this.recintoId = recinto.getId();
                    this.recintoNombre = recinto.getNombre();
                }

                Deporte deporte = cancha.getDeporte();
                if (deporte != null) {
                    this.deporteId = deporte.getIdDeporte();
                    this.deporteNombre = deporte.getNombre();
                }
            }

            EstadoReserva estado = reserva.getEstado();
            if (estado != null) {
                this.estadoId = estado.getIdEstado();
                this.estadoDescripcion = estado.getDescripcion();
            }
        }

        private String nombreCompleto(Usuario usuario) {
            String nombre = usuario.getNombre() != null ? usuario.getNombre() : "";
            String segundoNombre = usuario.getSegundoNombre() != null ? " " + usuario.getSegundoNombre() : "";
            String apellidoPaterno = usuario.getApellidoPaterno() != null ? " " + usuario.getApellidoPaterno() : "";
            String apellidoMaterno = usuario.getApellidoMaterno() != null ? " " + usuario.getApellidoMaterno() : "";

            return (nombre + segundoNombre + apellidoPaterno + apellidoMaterno)
                    .replaceAll("\\s+", " ")
                    .trim();
        }

        public Long getId() {
            return id;
        }

        public LocalDate getFechaUso() {
            return fechaUso;
        }

        public LocalTime getHoraInicio() {
            return horaInicio;
        }

        public LocalTime getHoraFin() {
            return horaFin;
        }

        public BigDecimal getMontoTotal() {
            return montoTotal;
        }

        public Long getUsuarioId() {
            return usuarioId;
        }

        public String getUsuarioNombre() {
            return usuarioNombre;
        }

        public String getUsuarioEmail() {
            return usuarioEmail;
        }

        public Long getCanchaId() {
            return canchaId;
        }

        public String getCanchaNombre() {
            return canchaNombre;
        }

        public Boolean getCanchaTechada() {
            return canchaTechada;
        }

        public String getCanchaSuperficie() {
            return canchaSuperficie;
        }

        public Long getRecintoId() {
            return recintoId;
        }

        public String getRecintoNombre() {
            return recintoNombre;
        }

        public Number getDeporteId() {
            return deporteId;
        }

        public String getDeporteNombre() {
            return deporteNombre;
        }

        public Long getEstadoId() {
            return estadoId;
        }

        public String getEstadoDescripcion() {
            return estadoDescripcion;
        }
    }
}
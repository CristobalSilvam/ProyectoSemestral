package com.example.searchsport.controller;

import com.example.searchsport.entity.Cancha;
import com.example.searchsport.entity.Recinto;
import com.example.searchsport.repository.RecintoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/admin/recintos")
@CrossOrigin(origins = "*")
public class AdminRecintoController {

    private final RecintoRepository recintoRepository;

    public AdminRecintoController(RecintoRepository recintoRepository) {
        this.recintoRepository = recintoRepository;
    }

    @GetMapping
    public ResponseEntity<List<RecintoResponse>> listarRecintos() {
        List<RecintoResponse> recintos = recintoRepository.findAll()
                .stream()
                .map(RecintoResponse::new)
                .toList();

        return ResponseEntity.ok(recintos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editarRecinto(
            @PathVariable Long id,
            @RequestBody EditarRecintoRequest request
    ) {
        Recinto recinto = recintoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recinto no encontrado"));

        recinto.setNombre(request.getNombre());
        recinto.setRutEmpresa(request.getRutEmpresa());
        recinto.setAprobado(request.getAprobado());

        recintoRepository.save(recinto);

        return ResponseEntity.ok(new RecintoResponse(recinto));
    }

    @PatchMapping("/{id}/aprobado")
    public ResponseEntity<?> cambiarEstadoAprobacion(
            @PathVariable Long id,
            @RequestBody CambiarAprobacionRequest request
    ) {
        Recinto recinto = recintoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recinto no encontrado"));

        recinto.setAprobado(request.getAprobado());
        recintoRepository.save(recinto);

        return ResponseEntity.ok(new RecintoResponse(recinto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarRecinto(@PathVariable Long id) {
        Recinto recinto = recintoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recinto no encontrado"));

        recintoRepository.delete(recinto);

        return ResponseEntity.noContent().build();
    }

    public static class EditarRecintoRequest {
        private String nombre;
        private String rutEmpresa;
        private Boolean aprobado;

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getRutEmpresa() {
            return rutEmpresa;
        }

        public void setRutEmpresa(String rutEmpresa) {
            this.rutEmpresa = rutEmpresa;
        }

        public Boolean getAprobado() {
            return aprobado;
        }

        public void setAprobado(Boolean aprobado) {
            this.aprobado = aprobado;
        }
    }

    public static class CambiarAprobacionRequest {
        private Boolean aprobado;

        public Boolean getAprobado() {
            return aprobado;
        }

        public void setAprobado(Boolean aprobado) {
            this.aprobado = aprobado;
        }
    }

    public static class RecintoResponse {
        private Long id;
        private String nombre;
        private String rutEmpresa;
        private Boolean aprobado;

        private String calle;
        private Integer numero;
        private String comuna;
        private String region;
        private BigDecimal latitud;
        private BigDecimal longitud;

        private Long usuarioId;
        private String usuarioEmail;
        private String usuarioNombre;

        private Integer cantidadCanchas;

        public RecintoResponse(Recinto recinto) {
            this.id = recinto.getId();
            this.nombre = recinto.getNombre();
            this.rutEmpresa = recinto.getRutEmpresa();
            this.aprobado = recinto.getAprobado();

            if (recinto.getDireccion() != null) {
                this.calle = recinto.getDireccion().getCalle();
                this.numero = recinto.getDireccion().getNumero();

                if (recinto.getDireccion().getComuna() != null) {
                    this.comuna = recinto.getDireccion().getComuna().getNombre();

                    if (recinto.getDireccion().getComuna().getRegion() != null) {
                        this.region = recinto.getDireccion().getComuna().getRegion().getNombre();
                    }
                }

                if (recinto.getDireccion().getCoordenada() != null) {
                    this.latitud = recinto.getDireccion().getCoordenada().getLatitud();
                    this.longitud = recinto.getDireccion().getCoordenada().getLongitud();
                }
            }

            if (recinto.getUsuario() != null) {
                this.usuarioId = recinto.getUsuario().getId();
                this.usuarioEmail = recinto.getUsuario().getEmail();
                this.usuarioNombre = recinto.getUsuario().getNombre() + " " + recinto.getUsuario().getApellidoPaterno();
            }

            if (recinto.getCanchas() != null) {
                this.cantidadCanchas = recinto.getCanchas().size();
            } else {
                this.cantidadCanchas = 0;
            }
        }

        public Long getId() {
            return id;
        }

        public String getNombre() {
            return nombre;
        }

        public String getRutEmpresa() {
            return rutEmpresa;
        }

        public Boolean getAprobado() {
            return aprobado;
        }

        public String getCalle() {
            return calle;
        }

        public Integer getNumero() {
            return numero;
        }

        public String getComuna() {
            return comuna;
        }

        public String getRegion() {
            return region;
        }

        public BigDecimal getLatitud() {
            return latitud;
        }

        public BigDecimal getLongitud() {
            return longitud;
        }

        public Long getUsuarioId() {
            return usuarioId;
        }

        public String getUsuarioEmail() {
            return usuarioEmail;
        }

        public String getUsuarioNombre() {
            return usuarioNombre;
        }

        public Integer getCantidadCanchas() {
            return cantidadCanchas;
        }
    }
}
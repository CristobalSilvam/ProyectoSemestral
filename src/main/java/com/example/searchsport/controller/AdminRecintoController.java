package com.example.searchsport.controller;

import com.example.searchsport.entity.Comuna;
import com.example.searchsport.entity.Direccion;
import com.example.searchsport.entity.Recinto;
import com.example.searchsport.entity.Region;
import com.example.searchsport.entity.Usuario;
import com.example.searchsport.repository.RecintoRepository;
import com.example.searchsport.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/recintos")
public class AdminRecintoController {

    private final RecintoRepository recintoRepository;
    private final UsuarioRepository usuarioRepository;

    public AdminRecintoController(
            RecintoRepository recintoRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.recintoRepository = recintoRepository;
        this.usuarioRepository = usuarioRepository;
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
    public ResponseEntity<RecintoResponse> actualizarRecinto(
            @PathVariable Long id,
            @RequestBody RecintoUpdateRequest request
    ) {
        Recinto recinto = recintoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recinto no encontrado"));

        recinto.setNombre(request.getNombre());
        recinto.setRutEmpresa(request.getRutEmpresa());
        recinto.setAprobado(Boolean.TRUE.equals(request.getAprobado()));

        if (request.getUsuarioId() == null) {
            recinto.setUsuario(null);
        } else {
            Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                    .orElseThrow(() -> new RuntimeException("Usuario dueño no encontrado"));

            recinto.setUsuario(usuario);
        }

        Recinto actualizado = recintoRepository.save(recinto);

        return ResponseEntity.ok(new RecintoResponse(actualizado));
    }

    @PatchMapping("/{id}/aprobado")
    public ResponseEntity<RecintoResponse> cambiarAprobacion(
            @PathVariable Long id,
            @RequestBody AprobacionRequest request
    ) {
        Recinto recinto = recintoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recinto no encontrado"));

        recinto.setAprobado(Boolean.TRUE.equals(request.getAprobado()));

        Recinto actualizado = recintoRepository.save(recinto);

        return ResponseEntity.ok(new RecintoResponse(actualizado));
    }

    public static class RecintoUpdateRequest {
        private String nombre;
        private String rutEmpresa;
        private Boolean aprobado;
        private Long usuarioId;

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

        public Long getUsuarioId() {
            return usuarioId;
        }

        public void setUsuarioId(Long usuarioId) {
            this.usuarioId = usuarioId;
        }
    }

    public static class AprobacionRequest {
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
        private Long usuarioId;
        private String usuarioNombre;
        private String usuarioEmail;
        private String calle;
        private String numero;
        private String comuna;
        private String region;
        private Integer cantidadCanchas;

        public RecintoResponse(Recinto recinto) {
            this.id = recinto.getId();
            this.nombre = recinto.getNombre();
            this.rutEmpresa = recinto.getRutEmpresa();
            this.aprobado = Boolean.TRUE.equals(recinto.getAprobado());

            Usuario usuario = recinto.getUsuario();

            if (usuario != null) {
                this.usuarioId = usuario.getId();
                this.usuarioNombre = nombreCompleto(usuario);
                this.usuarioEmail = usuario.getEmail();
            }

            Direccion direccion = recinto.getDireccion();

            if (direccion != null) {
                this.calle = direccion.getCalle();

                if (direccion.getNumero() != null) {
                    this.numero = String.valueOf(direccion.getNumero());
                }

                Comuna comunaEntity = direccion.getComuna();

                if (comunaEntity != null) {
                    this.comuna = comunaEntity.getNombre();

                    Region regionEntity = comunaEntity.getRegion();

                    if (regionEntity != null) {
                        this.region = regionEntity.getNombre();
                    }
                }
            }

            try {
                this.cantidadCanchas = recinto.getCanchas() != null
                        ? recinto.getCanchas().size()
                        : 0;
            } catch (Exception e) {
                this.cantidadCanchas = 0;
            }
        }

        private String nombreCompleto(Usuario usuario) {
            String nombre = usuario.getNombre() != null ? usuario.getNombre() : "";
            String segundoNombre = usuario.getSegundoNombre() != null ? " " + usuario.getSegundoNombre() : "";
            String apellidoPaterno = usuario.getApellidoPaterno() != null ? " " + usuario.getApellidoPaterno() : "";
            String apellidoMaterno = usuario.getApellidoMaterno() != null ? " " + usuario.getApellidoMaterno() : "";

            return (nombre + segundoNombre + apellidoPaterno + apellidoMaterno).trim();
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

        public Long getUsuarioId() {
            return usuarioId;
        }

        public String getUsuarioNombre() {
            return usuarioNombre;
        }

        public String getUsuarioEmail() {
            return usuarioEmail;
        }

        public String getCalle() {
            return calle;
        }

        public String getNumero() {
            return numero;
        }

        public String getComuna() {
            return comuna;
        }

        public String getRegion() {
            return region;
        }

        public Integer getCantidadCanchas() {
            return cantidadCanchas;
        }
    }
}
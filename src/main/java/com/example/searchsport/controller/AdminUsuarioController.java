package com.example.searchsport.controller;

import com.example.searchsport.entity.Rol;
import com.example.searchsport.entity.Usuario;
import com.example.searchsport.repository.RolRepository;
import com.example.searchsport.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/usuarios")
@CrossOrigin(origins = "*")
public class AdminUsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    public AdminUsuarioController(
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository
    ) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listarUsuarios() {
        List<UsuarioResponse> usuarios = usuarioRepository.findAll()
                .stream()
                .map(UsuarioResponse::new)
                .toList();

        return ResponseEntity.ok(usuarios);
    }

    @PatchMapping("/{id}/rol")
    public ResponseEntity<?> cambiarRol(
            @PathVariable Long id,
            @RequestBody CambiarRolRequest request
    ) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Rol rol = rolRepository.findById(request.getRolId())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        usuario.setRol(rol);
        usuarioRepository.save(usuario);

        return ResponseEntity.ok(new UsuarioResponse(usuario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setActivo(false);
        usuarioRepository.save(usuario);

        return ResponseEntity.noContent().build();
    }

    public static class CambiarRolRequest {
        private Long rolId;

        public Long getRolId() {
            return rolId;
        }

        public void setRolId(Long rolId) {
            this.rolId = rolId;
        }
    }

    public static class UsuarioResponse {
        private Long id;
        private String rut;
        private String nombre;
        private String segundoNombre;
        private String apellidoPaterno;
        private String apellidoMaterno;
        private String email;
        private Boolean activo;
        private Long rolId;
        private String rol;

        public UsuarioResponse(Usuario usuario) {
            this.id = usuario.getId();
            this.rut = usuario.getRut();
            this.nombre = usuario.getNombre();
            this.segundoNombre = usuario.getSegundoNombre();
            this.apellidoPaterno = usuario.getApellidoPaterno();
            this.apellidoMaterno = usuario.getApellidoMaterno();
            this.email = usuario.getEmail();
            this.activo = usuario.getActivo();

            if (usuario.getRol() != null) {
                this.rolId = usuario.getRol().getIdRol();
                this.rol = usuario.getRol().getNombre();
            }
        }

        public Long getId() {
            return id;
        }

        public String getRut() {
            return rut;
        }

        public String getNombre() {
            return nombre;
        }

        public String getSegundoNombre() {
            return segundoNombre;
        }

        public String getApellidoPaterno() {
            return apellidoPaterno;
        }

        public String getApellidoMaterno() {
            return apellidoMaterno;
        }

        public String getEmail() {
            return email;
        }

        public Boolean getActivo() {
            return activo;
        }

        public Long getRolId() {
            return rolId;
        }

        public String getRol() {
            return rol;
        }
    }
}
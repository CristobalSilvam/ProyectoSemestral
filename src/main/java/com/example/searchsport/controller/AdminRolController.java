package com.example.searchsport.controller;

import com.example.searchsport.entity.Rol;
import com.example.searchsport.repository.RolRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/roles")
@CrossOrigin(origins = "*")
public class AdminRolController {

    private final RolRepository rolRepository;

    public AdminRolController(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    @GetMapping
    public ResponseEntity<List<RolResponse>> listarRoles() {
        List<RolResponse> roles = rolRepository.findAll()
                .stream()
                .map(RolResponse::new)
                .toList();

        return ResponseEntity.ok(roles);
    }

    public static class RolResponse {
        private Long idRol;
        private String nombre;

        public RolResponse(Rol rol) {
            this.idRol = rol.getIdRol();
            this.nombre = rol.getNombre();
        }

        public Long getIdRol() {
            return idRol;
        }

        public String getNombre() {
            return nombre;
        }
    }
}
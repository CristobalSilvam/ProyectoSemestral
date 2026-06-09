package com.example.searchsport.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.searchsport.dto.RecintoMapaDTO;
import com.example.searchsport.dto.RecintoRequest;
import com.example.searchsport.entity.Recinto;
import com.example.searchsport.entity.Usuario;
import com.example.searchsport.repository.RecintoRepository;
import com.example.searchsport.repository.UsuarioRepository;
import com.example.searchsport.service.RecintoService;

@RestController
@RequestMapping("/api/recintos")
public class RecintoController {

    @Autowired
    private RecintoService recintoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RecintoRepository recintoRepository;

    // 1. Obtener mi recinto EXACTO (Por llave foránea)
    @GetMapping("/mi-recinto")
    public ResponseEntity<?> obtenerMiRecinto() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getName().equals("anonymousUser")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "No autenticado"));
        }
        String email = authentication.getName();

        Usuario dueno = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (dueno.getRol() == null || dueno.getRol().getIdRol() != 2L) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "No eres dueño"));
        }

        // Búsqueda real y definitiva en la BD
        Optional<Recinto> miRecinto = recintoRepository.findByUsuario_Id(dueno.getId());

        if (miRecinto.isPresent()) {
            return ResponseEntity.ok(miRecinto.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 2. CREACIÓN: Registrar un recinto y amarrarlo al Dueño
    @PostMapping
    public ResponseEntity<?> crearMiRecinto(@RequestBody RecintoRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            Usuario dueno = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Se crea el recinto usando la lógica que ya tenías
            Recinto nuevoRecinto = recintoService.crearRecinto(request);

            // VINCULACIÓN: Le asignamos la cuenta del dueño y guardamos el cambio
            nuevoRecinto.setUsuario(dueno);
            recintoRepository.save(nuevoRecinto);

            return new ResponseEntity<>(nuevoRecinto, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al crear recinto: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Recinto>> listarTodos() {
        List<Recinto> recintos = recintoService.obtenerTodos();
        return ResponseEntity.ok(recintos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recinto> obtenerPorId(@PathVariable Long id) {
        Optional<Recinto> recinto = recintoService.obtenerPorId(id);

        return recinto.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<RecintoMapaDTO>> buscarRecintos(
            @RequestParam(required = false) String deporte,
            @RequestParam(required = false) BigDecimal precioMax) {

        List<RecintoMapaDTO> resultados = recintoService.buscarParaMapa(deporte, precioMax);
        return ResponseEntity.ok(resultados);
    }
}
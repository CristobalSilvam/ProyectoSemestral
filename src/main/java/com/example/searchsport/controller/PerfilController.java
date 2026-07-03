package com.example.searchsport.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.searchsport.dto.ActualizarPerfilRequest;
import com.example.searchsport.dto.PerfilUsuarioResponse;
import com.example.searchsport.service.PerfilService;

@RestController
@RequestMapping("/api/usuarios")
public class PerfilController {

    @Autowired
    private PerfilService perfilService;

    @GetMapping("/me")
    public ResponseEntity<?> obtenerMiPerfil(Authentication authentication) {
        String emailUsuario = obtenerEmailUsuario(authentication);

        PerfilUsuarioResponse perfil = perfilService.obtenerMiPerfil(emailUsuario);

        return ResponseEntity.ok(perfil);
    }

    @PatchMapping("/me")
    public ResponseEntity<?> actualizarMiPerfil(
            @RequestBody ActualizarPerfilRequest request,
            Authentication authentication
    ) {
        String emailUsuario = obtenerEmailUsuario(authentication);

        PerfilUsuarioResponse perfil = perfilService.actualizarMiPerfil(emailUsuario, request);

        return ResponseEntity.ok(perfil);
    }

    private String obtenerEmailUsuario(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("Usuario no autenticado");
        }

        return authentication.getName();
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> manejarRuntimeException(RuntimeException e) {
        return ResponseEntity.badRequest().body(Map.of(
                "message", e.getMessage()
        ));
    }
}
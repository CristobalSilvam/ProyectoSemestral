package com.example.searchsport.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager; // NUEVO
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // NUEVO
import org.springframework.security.core.Authentication; // NUEVO
import org.springframework.security.core.context.SecurityContextHolder; // NUEVO
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.searchsport.dto.AuthResponse;
import com.example.searchsport.dto.LoginRequest;
import com.example.searchsport.dto.RegisterRequest; // NUEVO
import com.example.searchsport.entity.Rol;
import com.example.searchsport.entity.Usuario;
import com.example.searchsport.repository.UsuarioRepository;
import com.example.searchsport.util.JwtUtil;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    // --- NUEVAS INYECCIONES ---
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    // --------------------------

    // NUEVO ENDPOINT DE REGISTRO
    @PostMapping("/register")
    public ResponseEntity<?> registrar(@RequestBody RegisterRequest request) {
        
        // Verificamos si el email ya existe
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("El email ya está registrado");
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setRut(request.getRut());
        nuevoUsuario.setNombre(request.getNombre());
        nuevoUsuario.setSegundoNombre(request.getSegundoNombre());
        nuevoUsuario.setApellidoPaterno(request.getApellidoPaterno());
        nuevoUsuario.setApellidoMaterno(request.getApellidoMaterno());
        nuevoUsuario.setEmail(request.getEmail());
        nuevoUsuario.setPassword(passwordEncoder.encode(request.getPassword()));
        
        // Spring encripta la contraseña perfectamente
        nuevoUsuario.setPassword(passwordEncoder.encode(request.getPassword()));

        // Le asignamos el rol por defecto (1 = Cliente)
        // Le asignamos el rol
        Rol rolCliente = new Rol();
        rolCliente.setIdRol(1L); // O setId_rol(1L) dependiendo de cómo lo tengas en Rol.java
        nuevoUsuario.setRol(rolCliente);

        usuarioRepository.save(nuevoUsuario);

        return ResponseEntity.ok("Usuario registrado exitosamente");
    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtUtil.generarToken(loginRequest.getEmail());
        return ResponseEntity.ok(new AuthResponse(token));
    }
}
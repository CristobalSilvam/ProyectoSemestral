package com.example.searchsport.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.searchsport.dto.LoginRequest;
import com.example.searchsport.dto.RegisterRequest;
import com.example.searchsport.entity.Rol;
import com.example.searchsport.entity.Usuario;
import com.example.searchsport.repository.RolRepository;
import com.example.searchsport.repository.UsuarioRepository;
import com.example.searchsport.util.JwtUtil;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RolRepository rolRepository;

    @PostMapping("/register")
    public ResponseEntity<?> registrar(@RequestBody RegisterRequest request) {

        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "El correo ya está registrado")
            );
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setRut(request.getRut());
        nuevoUsuario.setNombre(request.getNombre());
        nuevoUsuario.setSegundoNombre(request.getSegundoNombre());
        nuevoUsuario.setApellidoPaterno(request.getApellidoPaterno());
        nuevoUsuario.setApellidoMaterno(request.getApellidoMaterno());
        nuevoUsuario.setEmail(request.getEmail());
        nuevoUsuario.setPassword(passwordEncoder.encode(request.getPassword()));

        // Rol 1 = Cliente / Usuario normal
        Rol rolCliente = rolRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado en la base de datos."));

        nuevoUsuario.setRol(rolCliente);

        usuarioRepository.save(nuevoUsuario);

        return ResponseEntity.ok(
                Map.of("message", "Usuario registrado exitosamente")
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        Usuario usuario = usuarioRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String token = jwtUtil.generarToken(usuario.getEmail());

        Long idRol = usuario.getRol().getIdRol();
        String nombreRol = usuario.getRol().getNombre();

        return ResponseEntity.ok(
                Map.of(
                        "token", token,
                        "email", usuario.getEmail(),
                        "nombre", usuario.getNombre(),
                        "rut", usuario.getRut(),
                        "idRol", idRol,
                        "id_rol", idRol,
                        "rol", nombreRol,
                        "role", nombreRol
                )
        );
    }
}
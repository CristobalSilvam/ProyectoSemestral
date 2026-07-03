package com.example.searchsport.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.searchsport.dto.ActualizarPerfilRequest;
import com.example.searchsport.dto.PerfilUsuarioResponse;
import com.example.searchsport.entity.Rol;
import com.example.searchsport.entity.Usuario;
import com.example.searchsport.repository.UsuarioRepository;

@Service
public class PerfilService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public PerfilUsuarioResponse obtenerMiPerfil(String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return convertirAResponse(usuario);
    }

    @Transactional
    public PerfilUsuarioResponse actualizarMiPerfil(
            String emailUsuario,
            ActualizarPerfilRequest request
    ) {
        if (request == null) {
            throw new RuntimeException("La solicitud no puede estar vacía");
        }

        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (request.getNombre() == null || request.getNombre().trim().isBlank()) {
            throw new RuntimeException("El nombre es obligatorio");
        }

        if (request.getApellidoPaterno() == null || request.getApellidoPaterno().trim().isBlank()) {
            throw new RuntimeException("El apellido paterno es obligatorio");
        }

        usuario.setNombre(request.getNombre().trim());
        usuario.setSegundoNombre(limpiarTextoOpcional(request.getSegundoNombre()));
        usuario.setApellidoPaterno(request.getApellidoPaterno().trim());
        usuario.setApellidoMaterno(limpiarTextoOpcional(request.getApellidoMaterno()));

        Usuario actualizado = usuarioRepository.save(usuario);

        return convertirAResponse(actualizado);
    }

    private String limpiarTextoOpcional(String valor) {
        if (valor == null || valor.trim().isBlank()) {
            return null;
        }

        return valor.trim();
    }

    private PerfilUsuarioResponse convertirAResponse(Usuario usuario) {
        Rol rol = usuario.getRol();

        Long rolId = rol != null ? rol.getIdRol() : null;
        String rolNombre = rol != null ? rol.getNombre() : null;

        return new PerfilUsuarioResponse(
                usuario.getId(),
                usuario.getRut(),
                usuario.getNombre(),
                usuario.getSegundoNombre(),
                usuario.getApellidoPaterno(),
                usuario.getApellidoMaterno(),
                usuario.getEmail(),
                usuario.getActivo(),
                rolId,
                rolNombre
        );
    }
}
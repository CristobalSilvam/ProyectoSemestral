package com.example.searchsport.controller;

import com.example.searchsport.entity.Rol;
import com.example.searchsport.entity.Usuario;
import com.example.searchsport.repository.RolRepository;
import com.example.searchsport.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminUsuarioControllerTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private AdminUsuarioController adminUsuarioController;

    @Test
    void listarUsuarios_debeRetornarUsuarios() {
        Rol rolCliente = new Rol();
        rolCliente.setIdRol(1L);
        rolCliente.setNombre("CLIENTE");

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setRut("11111111-1");
        usuario.setNombre("Cristobal");
        usuario.setApellidoPaterno("Silva");
        usuario.setEmail("cristobal@cliente.com");
        usuario.setActivo(true);
        usuario.setRol(rolCliente);

        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));

        ResponseEntity<List<AdminUsuarioController.UsuarioResponse>> response =
                adminUsuarioController.listarUsuarios();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());

        AdminUsuarioController.UsuarioResponse usuarioResponse = response.getBody().get(0);

        assertEquals(1L, usuarioResponse.getId());
        assertEquals("Cristobal", usuarioResponse.getNombre());
        assertEquals("cristobal@cliente.com", usuarioResponse.getEmail());
        assertEquals(true, usuarioResponse.getActivo());
        assertEquals(1L, usuarioResponse.getRolId());
        assertEquals("CLIENTE", usuarioResponse.getRol());

        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    void cambiarRol_debeActualizarRolDelUsuario() {
        Rol rolCliente = new Rol();
        rolCliente.setIdRol(1L);
        rolCliente.setNombre("CLIENTE");

        Rol rolAdmin = new Rol();
        rolAdmin.setIdRol(3L);
        rolAdmin.setNombre("ADMIN");

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setRut("11111111-1");
        usuario.setNombre("Cristobal");
        usuario.setApellidoPaterno("Silva");
        usuario.setEmail("cristobal@cliente.com");
        usuario.setActivo(true);
        usuario.setRol(rolCliente);

        AdminUsuarioController.RolUpdateRequest request =
                new AdminUsuarioController.RolUpdateRequest();
        request.setRolId(3L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(rolRepository.findById(3L)).thenReturn(Optional.of(rolAdmin));
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        ResponseEntity<AdminUsuarioController.UsuarioResponse> response =
                adminUsuarioController.cambiarRol(1L, request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(3L, response.getBody().getRolId());
        assertEquals("ADMIN", response.getBody().getRol());

        verify(usuarioRepository, times(1)).findById(1L);
        verify(rolRepository, times(1)).findById(3L);
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    void desactivarUsuario_debeCambiarActivoAFalse() {
        Rol rolCliente = new Rol();
        rolCliente.setIdRol(1L);
        rolCliente.setNombre("CLIENTE");

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setRut("11111111-1");
        usuario.setNombre("Cristobal");
        usuario.setApellidoPaterno("Silva");
        usuario.setEmail("cristobal@cliente.com");
        usuario.setActivo(true);
        usuario.setRol(rolCliente);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        ResponseEntity<Void> response = adminUsuarioController.desactivarUsuario(1L);

        assertEquals(204, response.getStatusCode().value());
        assertFalse(usuario.getActivo());

        verify(usuarioRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).save(usuario);
    }
}
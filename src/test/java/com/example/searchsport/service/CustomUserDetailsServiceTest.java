package com.example.searchsport.service;

import com.example.searchsport.entity.Rol;
import com.example.searchsport.entity.Usuario;
import com.example.searchsport.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUserByUsername_debeRetornarUserDetailsCuandoUsuarioExisteYEstaActivo() {
        Rol rol = new Rol();
        rol.setIdRol(1L);
        rol.setNombre("cliente");

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("cliente@email.com");
        usuario.setPassword("password-encriptada");
        usuario.setActivo(true);
        usuario.setRol(rol);

        when(usuarioRepository.findByEmail("cliente@email.com"))
                .thenReturn(Optional.of(usuario));

        UserDetails userDetails =
                customUserDetailsService.loadUserByUsername("cliente@email.com");

        assertNotNull(userDetails);
        assertEquals("cliente@email.com", userDetails.getUsername());
        assertEquals("password-encriptada", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());

        assertEquals(1, userDetails.getAuthorities().size());

        GrantedAuthority authority = userDetails.getAuthorities()
                .stream()
                .findFirst()
                .orElseThrow();

        assertEquals("CLIENTE", authority.getAuthority());

        verify(usuarioRepository, times(1)).findByEmail("cliente@email.com");
    }

    @Test
    void loadUserByUsername_debeRetornarUserDetailsConUsuarioInactivo() {
        Rol rol = new Rol();
        rol.setIdRol(2L);
        rol.setNombre("DUENIO");

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("duenio@email.com");
        usuario.setPassword("password-encriptada");
        usuario.setActivo(false);
        usuario.setRol(rol);

        when(usuarioRepository.findByEmail("duenio@email.com"))
                .thenReturn(Optional.of(usuario));

        UserDetails userDetails =
                customUserDetailsService.loadUserByUsername("duenio@email.com");

        assertNotNull(userDetails);
        assertEquals("duenio@email.com", userDetails.getUsername());
        assertEquals("password-encriptada", userDetails.getPassword());
        assertFalse(userDetails.isEnabled());

        GrantedAuthority authority = userDetails.getAuthorities()
                .stream()
                .findFirst()
                .orElseThrow();

        assertEquals("DUENIO", authority.getAuthority());

        verify(usuarioRepository, times(1)).findByEmail("duenio@email.com");
    }

    @Test
    void loadUserByUsername_debeRetornarUsuarioDeshabilitadoSiActivoEsNull() {
        Rol rol = new Rol();
        rol.setIdRol(1L);
        rol.setNombre("CLIENTE");

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("cliente@email.com");
        usuario.setPassword("password-encriptada");
        usuario.setActivo(null);
        usuario.setRol(rol);

        when(usuarioRepository.findByEmail("cliente@email.com"))
                .thenReturn(Optional.of(usuario));

        UserDetails userDetails =
                customUserDetailsService.loadUserByUsername("cliente@email.com");

        assertNotNull(userDetails);
        assertFalse(userDetails.isEnabled());

        GrantedAuthority authority = userDetails.getAuthorities()
                .stream()
                .findFirst()
                .orElseThrow();

        assertEquals("CLIENTE", authority.getAuthority());

        verify(usuarioRepository, times(1)).findByEmail("cliente@email.com");
    }

    @Test
    void loadUserByUsername_debeLanzarExcepcionSiUsuarioNoExiste() {
        when(usuarioRepository.findByEmail("noexiste@email.com"))
                .thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("noexiste@email.com")
        );

        assertEquals("Usuario no encontrado: noexiste@email.com", exception.getMessage());

        verify(usuarioRepository, times(1)).findByEmail("noexiste@email.com");
    }
}
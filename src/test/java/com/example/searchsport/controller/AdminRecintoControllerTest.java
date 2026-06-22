package com.example.searchsport.controller;

import com.example.searchsport.entity.Recinto;
import com.example.searchsport.entity.Rol;
import com.example.searchsport.entity.Usuario;
import com.example.searchsport.repository.RecintoRepository;
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
class AdminRecintoControllerTest {

    @Mock
    private RecintoRepository recintoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private AdminRecintoController adminRecintoController;

    @Test
    void listarRecintos_debeRetornarRecintos() {
        Recinto recinto = new Recinto();
        recinto.setId(1L);
        recinto.setNombre("Club Deportivo Providencia");
        recinto.setRutEmpresa("76345678-9");
        recinto.setAprobado(false);

        when(recintoRepository.findAll()).thenReturn(List.of(recinto));

        ResponseEntity<List<AdminRecintoController.RecintoResponse>> response =
                adminRecintoController.listarRecintos();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Club Deportivo Providencia", response.getBody().get(0).getNombre());
        assertFalse(response.getBody().get(0).getAprobado());

        verify(recintoRepository, times(1)).findAll();
    }

    @Test
    void cambiarAprobacion_debeAprobarRecinto() {
        Recinto recinto = new Recinto();
        recinto.setId(1L);
        recinto.setNombre("Club Deportivo Providencia");
        recinto.setRutEmpresa("76345678-9");
        recinto.setAprobado(false);

        AdminRecintoController.AprobacionRequest request =
                new AdminRecintoController.AprobacionRequest();
        request.setAprobado(true);

        when(recintoRepository.findById(1L)).thenReturn(Optional.of(recinto));
        when(recintoRepository.save(recinto)).thenReturn(recinto);

        ResponseEntity<AdminRecintoController.RecintoResponse> response =
                adminRecintoController.cambiarAprobacion(1L, request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getAprobado());

        verify(recintoRepository, times(1)).findById(1L);
        verify(recintoRepository, times(1)).save(recinto);
    }

    @Test
    void actualizarRecinto_debeCambiarDuenioDelRecinto() {
        Recinto recinto = new Recinto();
        recinto.setId(1L);
        recinto.setNombre("Club Deportivo Providencia");
        recinto.setRutEmpresa("76345678-9");
        recinto.setAprobado(false);

        Rol rolDuenio = new Rol();
        rolDuenio.setIdRol(2L);
        rolDuenio.setNombre("DUENO");

        Usuario duenio = new Usuario();
        duenio.setId(13L);
        duenio.setNombre("Cristobal");
        duenio.setApellidoPaterno("Silva");
        duenio.setEmail("cris@dueno.com");
        duenio.setActivo(true);
        duenio.setRol(rolDuenio);

        AdminRecintoController.RecintoUpdateRequest request =
                new AdminRecintoController.RecintoUpdateRequest();
        request.setNombre("Club Deportivo Providencia");
        request.setRutEmpresa("76345678-9");
        request.setAprobado(true);
        request.setUsuarioId(13L);

        when(recintoRepository.findById(1L)).thenReturn(Optional.of(recinto));
        when(usuarioRepository.findById(13L)).thenReturn(Optional.of(duenio));
        when(recintoRepository.save(recinto)).thenReturn(recinto);

        ResponseEntity<AdminRecintoController.RecintoResponse> response =
                adminRecintoController.actualizarRecinto(1L, request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(13L, response.getBody().getUsuarioId());
        assertEquals("Cristobal Silva", response.getBody().getUsuarioNombre());
        assertEquals("cris@dueno.com", response.getBody().getUsuarioEmail());
        assertTrue(response.getBody().getAprobado());

        verify(recintoRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).findById(13L);
        verify(recintoRepository, times(1)).save(recinto);
    }
}
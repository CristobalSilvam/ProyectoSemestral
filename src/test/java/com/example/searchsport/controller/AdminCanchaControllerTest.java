package com.example.searchsport.controller;

import com.example.searchsport.entity.Cancha;
import com.example.searchsport.entity.Deporte;
import com.example.searchsport.entity.Recinto;
import com.example.searchsport.repository.CanchaRepository;
import com.example.searchsport.repository.DeporteRepository;
import com.example.searchsport.repository.RecintoRepository;
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
class AdminCanchaControllerTest {

    @Mock
    private CanchaRepository canchaRepository;

    @Mock
    private RecintoRepository recintoRepository;

    @Mock
    private DeporteRepository deporteRepository;

    @InjectMocks
    private AdminCanchaController adminCanchaController;

    @Test
    void listarCanchas_debeRetornarCanchas() {
        Recinto recinto = new Recinto();
        recinto.setId(1L);
        recinto.setNombre("Complejo Deportivo Central");
        recinto.setAprobado(true);

        Deporte deporte = new Deporte();
        deporte.setNombre("Fútbol");

        Cancha cancha = new Cancha();
        cancha.setIdCancha(1L);
        cancha.setNombreInterno("Cancha 1");
        cancha.setEsTechada(true);
        cancha.setTipoSuperficie("Pasto sintético");
        cancha.setRecinto(recinto);
        cancha.setDeporte(deporte);

        when(canchaRepository.findAll()).thenReturn(List.of(cancha));

        ResponseEntity<List<AdminCanchaController.CanchaResponse>> response =
                adminCanchaController.listarCanchas();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());

        AdminCanchaController.CanchaResponse canchaResponse = response.getBody().get(0);

        assertEquals(1L, canchaResponse.getId());
        assertEquals("Cancha 1", canchaResponse.getNombreInterno());
        assertTrue(canchaResponse.getEsTechada());
        assertEquals("Pasto sintético", canchaResponse.getTipoSuperficie());
        assertEquals(1L, canchaResponse.getRecintoId());
        assertEquals("Complejo Deportivo Central", canchaResponse.getRecintoNombre());
        assertTrue(canchaResponse.getRecintoAprobado());
        assertEquals("Fútbol", canchaResponse.getDeporteNombre());

        verify(canchaRepository, times(1)).findAll();
    }

    @Test
    void obtenerOpciones_debeRetornarRecintosYDeportes() {
        Recinto recinto = new Recinto();
        recinto.setId(1L);
        recinto.setNombre("Complejo Deportivo Central");
        recinto.setAprobado(true);

        Deporte deporte = new Deporte();
        deporte.setNombre("Fútbol");

        when(recintoRepository.findAll()).thenReturn(List.of(recinto));
        when(deporteRepository.findAll()).thenReturn(List.of(deporte));

        ResponseEntity<AdminCanchaController.CanchaOpcionesResponse> response =
                adminCanchaController.obtenerOpciones();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());

        assertEquals(1, response.getBody().getRecintos().size());
        assertEquals(1, response.getBody().getDeportes().size());

        assertEquals("Complejo Deportivo Central", response.getBody().getRecintos().get(0).getNombre());
        assertTrue(response.getBody().getRecintos().get(0).getAprobado());

        assertEquals("Fútbol", response.getBody().getDeportes().get(0).getNombre());

        verify(recintoRepository, times(1)).findAll();
        verify(deporteRepository, times(1)).findAll();
    }

    @Test
    void actualizarCancha_debeGuardarCambios() {
        Recinto recinto = new Recinto();
        recinto.setId(1L);
        recinto.setNombre("Complejo Deportivo Central");
        recinto.setAprobado(true);

        Deporte deporte = new Deporte();
        deporte.setNombre("Fútbol");

        Cancha cancha = new Cancha();
        cancha.setIdCancha(1L);
        cancha.setNombreInterno("Cancha antigua");
        cancha.setEsTechada(false);
        cancha.setTipoSuperficie("Cemento");

        AdminCanchaController.CanchaUpdateRequest request =
                new AdminCanchaController.CanchaUpdateRequest();

        request.setNombreInterno("Cancha renovada");
        request.setEsTechada(true);
        request.setTipoSuperficie("Pasto sintético");
        request.setRecintoId(1L);
        request.setDeporteId(1);

        when(canchaRepository.findById(1L)).thenReturn(Optional.of(cancha));
        when(recintoRepository.findById(1L)).thenReturn(Optional.of(recinto));
        when(deporteRepository.findById(1)).thenReturn(Optional.of(deporte));
        when(canchaRepository.save(cancha)).thenReturn(cancha);

        ResponseEntity<AdminCanchaController.CanchaResponse> response =
                adminCanchaController.actualizarCancha(1L, request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());

        assertEquals("Cancha renovada", response.getBody().getNombreInterno());
        assertTrue(response.getBody().getEsTechada());
        assertEquals("Pasto sintético", response.getBody().getTipoSuperficie());
        assertEquals("Complejo Deportivo Central", response.getBody().getRecintoNombre());
        assertEquals("Fútbol", response.getBody().getDeporteNombre());

        verify(canchaRepository, times(1)).findById(1L);
        verify(recintoRepository, times(1)).findById(1L);
        verify(deporteRepository, times(1)).findById(1);
        verify(canchaRepository, times(1)).save(cancha);
    }

    @Test
    void actualizarCancha_debeLanzarExcepcionSiCanchaNoExiste() {
        AdminCanchaController.CanchaUpdateRequest request =
                new AdminCanchaController.CanchaUpdateRequest();

        request.setNombreInterno("Cancha renovada");
        request.setEsTechada(true);
        request.setTipoSuperficie("Pasto sintético");
        request.setRecintoId(1L);
        request.setDeporteId(1);

        when(canchaRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> adminCanchaController.actualizarCancha(99L, request)
        );

        assertEquals("Cancha no encontrada", exception.getMessage());

        verify(canchaRepository, times(1)).findById(99L);
        verify(recintoRepository, never()).findById(anyLong());
        verify(deporteRepository, never()).findById(anyInt());
        verify(canchaRepository, never()).save(any(Cancha.class));
    }

    @Test
    void actualizarCancha_debeLanzarExcepcionSiRecintoNoExiste() {
        Cancha cancha = new Cancha();
        cancha.setIdCancha(1L);
        cancha.setNombreInterno("Cancha antigua");
        cancha.setEsTechada(false);
        cancha.setTipoSuperficie("Cemento");

        AdminCanchaController.CanchaUpdateRequest request =
                new AdminCanchaController.CanchaUpdateRequest();

        request.setNombreInterno("Cancha renovada");
        request.setEsTechada(true);
        request.setTipoSuperficie("Pasto sintético");
        request.setRecintoId(99L);
        request.setDeporteId(1);

        when(canchaRepository.findById(1L)).thenReturn(Optional.of(cancha));
        when(recintoRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> adminCanchaController.actualizarCancha(1L, request)
        );

        assertEquals("Recinto no encontrado", exception.getMessage());

        verify(canchaRepository, times(1)).findById(1L);
        verify(recintoRepository, times(1)).findById(99L);
        verify(deporteRepository, never()).findById(anyInt());
        verify(canchaRepository, never()).save(any(Cancha.class));
    }

    @Test
    void actualizarCancha_debeLanzarExcepcionSiDeporteNoExiste() {
        Recinto recinto = new Recinto();
        recinto.setId(1L);
        recinto.setNombre("Complejo Deportivo Central");
        recinto.setAprobado(true);

        Cancha cancha = new Cancha();
        cancha.setIdCancha(1L);
        cancha.setNombreInterno("Cancha antigua");
        cancha.setEsTechada(false);
        cancha.setTipoSuperficie("Cemento");

        AdminCanchaController.CanchaUpdateRequest request =
                new AdminCanchaController.CanchaUpdateRequest();

        request.setNombreInterno("Cancha renovada");
        request.setEsTechada(true);
        request.setTipoSuperficie("Pasto sintético");
        request.setRecintoId(1L);
        request.setDeporteId(99);

        when(canchaRepository.findById(1L)).thenReturn(Optional.of(cancha));
        when(recintoRepository.findById(1L)).thenReturn(Optional.of(recinto));
        when(deporteRepository.findById(99)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> adminCanchaController.actualizarCancha(1L, request)
        );

        assertEquals("Deporte no encontrado", exception.getMessage());

        verify(canchaRepository, times(1)).findById(1L);
        verify(recintoRepository, times(1)).findById(1L);
        verify(deporteRepository, times(1)).findById(99);
        verify(canchaRepository, never()).save(any(Cancha.class));
    }
}
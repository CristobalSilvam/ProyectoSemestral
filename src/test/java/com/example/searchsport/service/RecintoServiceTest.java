package com.example.searchsport.service;

import com.example.searchsport.dto.RecintoMapaDTO;
import com.example.searchsport.dto.RecintoRequest;
import com.example.searchsport.entity.Comuna;
import com.example.searchsport.entity.Coordenada;
import com.example.searchsport.entity.Direccion;
import com.example.searchsport.entity.Recinto;
import com.example.searchsport.entity.Region;
import com.example.searchsport.repository.ComunaRepository;
import com.example.searchsport.repository.CoordenadaRepository;
import com.example.searchsport.repository.DireccionRepository;
import com.example.searchsport.repository.RecintoRepository;
import com.example.searchsport.repository.RegionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecintoServiceTest {

    @Mock
    private RecintoRepository recintoRepository;

    @Mock
    private DireccionRepository direccionRepository;

    @Mock
    private CoordenadaRepository coordenadaRepository;

    @Mock
    private ComunaRepository comunaRepository;

    @Mock
    private RegionRepository regionRepository;

    @InjectMocks
    private RecintoService recintoService;

    @Test
    void buscarParaMapa_debeRetornarRecintosFiltrados() {
        String deporte = "Fútbol";
        BigDecimal precioMax = new BigDecimal("30000");

        when(recintoRepository.buscarRecintosParaMapa(deporte, precioMax))
                .thenReturn(List.of());

        List<RecintoMapaDTO> resultado = recintoService.buscarParaMapa(deporte, precioMax);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());

        verify(recintoRepository, times(1)).buscarRecintosParaMapa(deporte, precioMax);
    }

    @Test
    void obtenerTodos_debeRetornarTodosLosRecintos() {
        Recinto recinto = new Recinto();
        recinto.setId(1L);
        recinto.setNombre("Complejo Deportivo Central");
        recinto.setRutEmpresa("76.123.456-7");

        when(recintoRepository.findAll()).thenReturn(List.of(recinto));

        List<Recinto> resultado = recintoService.obtenerTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
        assertEquals("Complejo Deportivo Central", resultado.get(0).getNombre());
        assertEquals("76.123.456-7", resultado.get(0).getRutEmpresa());

        verify(recintoRepository, times(1)).findAll();
    }

    @Test
    void obtenerPorId_debeRetornarRecintoCuandoExiste() {
        Recinto recinto = new Recinto();
        recinto.setId(1L);
        recinto.setNombre("Complejo Deportivo Central");

        when(recintoRepository.findById(1L)).thenReturn(Optional.of(recinto));

        Optional<Recinto> resultado = recintoService.obtenerPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
        assertEquals("Complejo Deportivo Central", resultado.get().getNombre());

        verify(recintoRepository, times(1)).findById(1L);
    }

    @Test
    void obtenerPorId_debeRetornarOptionalVacioCuandoNoExiste() {
        when(recintoRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Recinto> resultado = recintoService.obtenerPorId(99L);

        assertTrue(resultado.isEmpty());

        verify(recintoRepository, times(1)).findById(99L);
    }

    @Test
    void guardar_debeGuardarRecinto() {
        Recinto recinto = new Recinto();
        recinto.setId(1L);
        recinto.setNombre("Complejo Deportivo Central");
        recinto.setRutEmpresa("76.123.456-7");

        when(recintoRepository.save(recinto)).thenReturn(recinto);

        Recinto resultado = recintoService.guardar(recinto);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Complejo Deportivo Central", resultado.getNombre());
        assertEquals("76.123.456-7", resultado.getRutEmpresa());

        verify(recintoRepository, times(1)).save(recinto);
    }

    @Test
    void crearRecinto_debeCrearRecintoCorrectamente() {
        RecintoRequest request = new RecintoRequest();
        request.setNombre("Complejo Deportivo Central");
        request.setRutEmpresa("76.123.456-7");
        request.setCalle("Av. Principal");
        request.setRegion("Metropolitana");
        request.setComuna("Santiago");
        request.setLatitud(new BigDecimal("-33.44890000"));
        request.setLongitud(new BigDecimal("-70.66930000"));

        Coordenada coordenadaGuardada = new Coordenada();
        coordenadaGuardada.setId(1L);
        coordenadaGuardada.setLatitud(request.getLatitud());
        coordenadaGuardada.setLongitud(request.getLongitud());

        Region region = new Region();
        region.setId(1L);
        region.setNombre("Metropolitana");

        Comuna comuna = new Comuna();
        comuna.setId(1L);
        comuna.setNombre("Santiago");
        comuna.setRegion(region);

        Direccion direccionGuardada = new Direccion();
        direccionGuardada.setId(1L);
        direccionGuardada.setCalle("Av. Principal");
        direccionGuardada.setComuna(comuna);
        direccionGuardada.setCoordenada(coordenadaGuardada);

        when(coordenadaRepository.save(any(Coordenada.class)))
                .thenReturn(coordenadaGuardada);

        when(regionRepository.findByNombre("Metropolitana"))
                .thenReturn(Optional.of(region));

        when(comunaRepository.findByNombre("Santiago"))
                .thenReturn(Optional.of(comuna));

        when(direccionRepository.save(any(Direccion.class)))
                .thenReturn(direccionGuardada);

        when(recintoRepository.save(any(Recinto.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Recinto resultado = recintoService.crearRecinto(request);

        assertNotNull(resultado);
        assertEquals("Complejo Deportivo Central", resultado.getNombre());
        assertEquals("76.123.456-7", resultado.getRutEmpresa());
        assertNotNull(resultado.getDireccion());
        assertEquals("Av. Principal", resultado.getDireccion().getCalle());
        assertEquals("Santiago", resultado.getDireccion().getComuna().getNombre());
        assertEquals(new BigDecimal("-33.44890000"), resultado.getDireccion().getCoordenada().getLatitud());
        assertEquals(new BigDecimal("-70.66930000"), resultado.getDireccion().getCoordenada().getLongitud());

        verify(coordenadaRepository, times(1)).save(any(Coordenada.class));
        verify(regionRepository, times(1)).findByNombre("Metropolitana");
        verify(comunaRepository, times(1)).findByNombre("Santiago");
        verify(direccionRepository, times(1)).save(any(Direccion.class));
        verify(recintoRepository, times(1)).save(any(Recinto.class));
    }

    @Test
    void crearRecinto_debeLanzarExcepcionSiRegionNoExiste() {
        RecintoRequest request = new RecintoRequest();
        request.setNombre("Complejo Deportivo Central");
        request.setRutEmpresa("76.123.456-7");
        request.setCalle("Av. Principal");
        request.setRegion("Region Inexistente");
        request.setComuna("Santiago");
        request.setLatitud(new BigDecimal("-33.44890000"));
        request.setLongitud(new BigDecimal("-70.66930000"));

        Coordenada coordenadaGuardada = new Coordenada();
        coordenadaGuardada.setId(1L);
        coordenadaGuardada.setLatitud(request.getLatitud());
        coordenadaGuardada.setLongitud(request.getLongitud());

        when(coordenadaRepository.save(any(Coordenada.class)))
                .thenReturn(coordenadaGuardada);

        when(regionRepository.findByNombre("Region Inexistente"))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> recintoService.crearRecinto(request)
        );

        assertEquals("Región no encontrada", exception.getMessage());

        verify(coordenadaRepository, times(1)).save(any(Coordenada.class));
        verify(regionRepository, times(1)).findByNombre("Region Inexistente");
        verify(comunaRepository, never()).findByNombre(anyString());
        verify(direccionRepository, never()).save(any(Direccion.class));
        verify(recintoRepository, never()).save(any(Recinto.class));
    }

    @Test
    void crearRecinto_debeLanzarExcepcionSiComunaNoExiste() {
        RecintoRequest request = new RecintoRequest();
        request.setNombre("Complejo Deportivo Central");
        request.setRutEmpresa("76.123.456-7");
        request.setCalle("Av. Principal");
        request.setRegion("Metropolitana");
        request.setComuna("Comuna Inexistente");
        request.setLatitud(new BigDecimal("-33.44890000"));
        request.setLongitud(new BigDecimal("-70.66930000"));

        Coordenada coordenadaGuardada = new Coordenada();
        coordenadaGuardada.setId(1L);
        coordenadaGuardada.setLatitud(request.getLatitud());
        coordenadaGuardada.setLongitud(request.getLongitud());

        Region region = new Region();
        region.setId(1L);
        region.setNombre("Metropolitana");

        when(coordenadaRepository.save(any(Coordenada.class)))
                .thenReturn(coordenadaGuardada);

        when(regionRepository.findByNombre("Metropolitana"))
                .thenReturn(Optional.of(region));

        when(comunaRepository.findByNombre("Comuna Inexistente"))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> recintoService.crearRecinto(request)
        );

        assertEquals("Comuna no encontrada", exception.getMessage());

        verify(coordenadaRepository, times(1)).save(any(Coordenada.class));
        verify(regionRepository, times(1)).findByNombre("Metropolitana");
        verify(comunaRepository, times(1)).findByNombre("Comuna Inexistente");
        verify(direccionRepository, never()).save(any(Direccion.class));
        verify(recintoRepository, never()).save(any(Recinto.class));
    }
}
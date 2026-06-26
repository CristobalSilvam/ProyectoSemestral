package com.example.searchsport.service;

import com.example.searchsport.entity.Imagen;
import com.example.searchsport.entity.Recinto;
import com.example.searchsport.repository.ImagenRepository;
import com.example.searchsport.repository.RecintoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImagenServiceTest {

    @Mock
    private ImagenRepository imagenRepository;

    @Mock
    private RecintoRepository recintoRepository;

    @InjectMocks
    private ImagenService imagenService;

    @AfterEach
    void limpiarUploads() throws IOException {
        Path uploads = Path.of("uploads");

        if (Files.exists(uploads)) {
            try (Stream<Path> archivos = Files.list(uploads)) {
                archivos
                        .filter(path -> path.getFileName().toString().endsWith(".jpg")
                                || path.getFileName().toString().endsWith(".png"))
                        .forEach(path -> {
                            try {
                                Files.deleteIfExists(path);
                            } catch (IOException ignored) {
                            }
                        });
            }
        }
    }

    @Test
    void subirImagen_debeGuardarImagenJpgCorrectamente() throws IOException {
        Recinto recinto = new Recinto();
        recinto.setId(1L);
        recinto.setNombre("Cancha Central");

        MockMultipartFile archivo = new MockMultipartFile(
                "archivo",
                "cancha.jpg",
                "image/jpeg",
                "contenido-imagen".getBytes()
        );

        when(recintoRepository.findById(1L)).thenReturn(Optional.of(recinto));
        when(imagenRepository.save(any(Imagen.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Imagen resultado = imagenService.subirImagen(1L, archivo);

        assertNotNull(resultado);
        assertNotNull(resultado.getUrl());
        assertTrue(resultado.getUrl().startsWith("/uploads/"));
        assertTrue(resultado.getUrl().endsWith(".jpg"));
        assertEquals(recinto, resultado.getRecinto());

        verify(recintoRepository).findById(1L);
        verify(imagenRepository).save(any(Imagen.class));
    }

    @Test
    void subirImagen_debeGuardarImagenPngCorrectamente() throws IOException {
        Recinto recinto = new Recinto();
        recinto.setId(1L);
        recinto.setNombre("Cancha Central");

        MockMultipartFile archivo = new MockMultipartFile(
                "archivo",
                "cancha.png",
                "image/png",
                "contenido-imagen".getBytes()
        );

        when(recintoRepository.findById(1L)).thenReturn(Optional.of(recinto));
        when(imagenRepository.save(any(Imagen.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Imagen resultado = imagenService.subirImagen(1L, archivo);

        assertNotNull(resultado);
        assertNotNull(resultado.getUrl());
        assertTrue(resultado.getUrl().startsWith("/uploads/"));
        assertTrue(resultado.getUrl().endsWith(".png"));
        assertEquals(recinto, resultado.getRecinto());

        verify(recintoRepository).findById(1L);
        verify(imagenRepository).save(any(Imagen.class));
    }

    @Test
    void subirImagen_debeUsarNombrePorDefectoSiArchivoNoTieneExtension() throws IOException {
        Recinto recinto = new Recinto();
        recinto.setId(1L);
        recinto.setNombre("Cancha Central");

        MockMultipartFile archivo = new MockMultipartFile(
                "archivo",
                "cancha",
                "image/jpeg",
                "contenido-imagen".getBytes()
        );

        when(recintoRepository.findById(1L)).thenReturn(Optional.of(recinto));
        when(imagenRepository.save(any(Imagen.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Imagen resultado = imagenService.subirImagen(1L, archivo);

        assertNotNull(resultado);
        assertTrue(resultado.getUrl().startsWith("/uploads/"));
        assertTrue(resultado.getUrl().endsWith(".jpg"));

        verify(recintoRepository).findById(1L);
        verify(imagenRepository).save(any(Imagen.class));
    }

    @Test
    void subirImagen_debeLanzarExcepcionSiRecintoNoExiste() {
        MockMultipartFile archivo = new MockMultipartFile(
                "archivo",
                "cancha.jpg",
                "image/jpeg",
                "contenido-imagen".getBytes()
        );

        when(recintoRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> imagenService.subirImagen(99L, archivo)
        );

        assertEquals("Recinto no encontrado", exception.getMessage());

        verify(recintoRepository).findById(99L);
        verify(imagenRepository, never()).save(any(Imagen.class));
    }

    @Test
    void subirImagen_debeLanzarExcepcionSiContentTypeEsNull() {
        Recinto recinto = new Recinto();
        recinto.setId(1L);

        MockMultipartFile archivo = new MockMultipartFile(
                "archivo",
                "cancha.jpg",
                null,
                "contenido-imagen".getBytes()
        );

        when(recintoRepository.findById(1L)).thenReturn(Optional.of(recinto));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> imagenService.subirImagen(1L, archivo)
        );

        assertEquals("Solo se permiten archivos JPG o PNG", exception.getMessage());

        verify(recintoRepository).findById(1L);
        verify(imagenRepository, never()).save(any(Imagen.class));
    }

    @Test
    void subirImagen_debeLanzarExcepcionSiArchivoNoEsJpgNiPng() {
        Recinto recinto = new Recinto();
        recinto.setId(1L);

        MockMultipartFile archivo = new MockMultipartFile(
                "archivo",
                "documento.pdf",
                "application/pdf",
                "contenido".getBytes()
        );

        when(recintoRepository.findById(1L)).thenReturn(Optional.of(recinto));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> imagenService.subirImagen(1L, archivo)
        );

        assertEquals("Solo se permiten archivos JPG o PNG", exception.getMessage());

        verify(recintoRepository).findById(1L);
        verify(imagenRepository, never()).save(any(Imagen.class));
    }
}
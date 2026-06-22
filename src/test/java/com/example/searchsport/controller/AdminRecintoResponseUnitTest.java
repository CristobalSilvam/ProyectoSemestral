package com.example.searchsport.controller;

import com.example.searchsport.entity.Comuna;
import com.example.searchsport.entity.Direccion;
import com.example.searchsport.entity.Recinto;
import com.example.searchsport.entity.Region;
import com.example.searchsport.entity.Rol;
import com.example.searchsport.entity.Usuario;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdminRecintoResponseUnitTest {

    @Test
    void recintoResponse_debeMapearDatosDelRecintoCorrectamente() {
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

        Region region = new Region();
        region.setNombre("Región Metropolitana de Santiago");

        Comuna comuna = new Comuna();
        comuna.setNombre("Providencia");
        comuna.setRegion(region);

        Direccion direccion = new Direccion();
        direccion.setCalle("Antonio Varas");
        direccion.setNumero(666);
        direccion.setComuna(comuna);

        Recinto recinto = new Recinto();
        recinto.setId(7L);
        recinto.setNombre("DuocUc");
        recinto.setRutEmpresa("76.123.456-k");
        recinto.setAprobado(true);
        recinto.setUsuario(duenio);
        recinto.setDireccion(direccion);

        AdminRecintoController.RecintoResponse response =
                new AdminRecintoController.RecintoResponse(recinto);

        assertEquals(7L, response.getId());
        assertEquals("DuocUc", response.getNombre());
        assertEquals("76.123.456-k", response.getRutEmpresa());
        assertTrue(response.getAprobado());

        assertEquals(13L, response.getUsuarioId());
        assertEquals("Cristobal Silva", response.getUsuarioNombre());
        assertEquals("cris@dueno.com", response.getUsuarioEmail());

        assertEquals("Antonio Varas", response.getCalle());
        assertEquals("666", response.getNumero());
        assertEquals("Providencia", response.getComuna());
        assertEquals("Región Metropolitana de Santiago", response.getRegion());
        assertEquals(0, response.getCantidadCanchas());
    }

    @Test
    void recintoResponse_debeSoportarRecintoSinDuenio() {
        Recinto recinto = new Recinto();
        recinto.setId(1L);
        recinto.setNombre("Complejo Deportivo Santiago Centro");
        recinto.setRutEmpresa("76123456-7");
        recinto.setAprobado(false);

        AdminRecintoController.RecintoResponse response =
                new AdminRecintoController.RecintoResponse(recinto);

        assertEquals(1L, response.getId());
        assertEquals("Complejo Deportivo Santiago Centro", response.getNombre());
        assertEquals("76123456-7", response.getRutEmpresa());
        assertFalse(response.getAprobado());

        assertNull(response.getUsuarioId());
        assertNull(response.getUsuarioNombre());
        assertNull(response.getUsuarioEmail());
        assertEquals(0, response.getCantidadCanchas());
    }
}
package com.example.searchsport.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.searchsport.dto.BloqueoRequest;
import com.example.searchsport.dto.CanchaRequest;
import com.example.searchsport.dto.RecintoRequest;
import com.example.searchsport.dto.ReporteIngresosDTO;
import com.example.searchsport.dto.TarifaRequest;
import com.example.searchsport.entity.Cancha;
import com.example.searchsport.entity.HorarioEspecial;
import com.example.searchsport.entity.Recinto;
import com.example.searchsport.entity.Tarifa;
import com.example.searchsport.service.CanchaService;
import com.example.searchsport.service.RecintoService;
import com.example.searchsport.service.ReporteService;
import com.example.searchsport.service.TarifaService;
import com.example.searchsport.service.BloqueoService;
import org.springframework.web.multipart.MultipartFile;
import com.example.searchsport.service.ImagenService;
import com.example.searchsport.entity.Imagen;


@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private RecintoService recintoService;

    @Autowired
    private CanchaService canchaService;

    @Autowired
    private TarifaService tarifaService;

    @Autowired
    private ReporteService reporteService;

    @Autowired
    private BloqueoService bloqueoService;

    @Autowired
    private ImagenService imagenService;

    @PostMapping("/recintos")
    public ResponseEntity<Recinto> crearRecinto(@RequestBody RecintoRequest request) {
        Recinto nuevoRecinto = recintoService.crearRecinto(request);
        return new ResponseEntity<>(nuevoRecinto, HttpStatus.CREATED);
    }

    @PostMapping("/canchas")
    public ResponseEntity<Cancha> crearCancha(@RequestBody CanchaRequest request) {
        Cancha nuevaCancha = canchaService.guardarCancha(request);
        return new ResponseEntity<>(nuevaCancha, HttpStatus.CREATED);
    }

    @PostMapping("/tarifas")
    public ResponseEntity<Tarifa> crearTarifa(@RequestBody TarifaRequest request) {
        Tarifa nuevaTarifa = tarifaService.guardarTarifa(request);
        return new ResponseEntity<>(nuevaTarifa, HttpStatus.CREATED);
    }
    // Dashboard de Ingresos
    @GetMapping("/reportes/ingresos")
    public ResponseEntity<ReporteIngresosDTO> reporteIngresos(
            @RequestParam Long recintoId,
            @RequestParam int mes,
            @RequestParam int anio) {
        
        ReporteIngresosDTO reporte = reporteService.generarReporteMensual(recintoId, mes, anio);
        return ResponseEntity.ok(reporte);
    }
    // Crear un bloqueo por mantenimiento
    @PostMapping("/bloqueos")
    public ResponseEntity<HorarioEspecial> crearBloqueo(@RequestBody BloqueoRequest request) {
        HorarioEspecial nuevoBloqueo = bloqueoService.crearBloqueo(request);
        return new ResponseEntity<>(nuevoBloqueo, HttpStatus.CREATED);
    }

    // Ver bloqueos activos de una cancha
    @GetMapping("/bloqueos/{canchaId}")
    public ResponseEntity<List<HorarioEspecial>> verBloqueos(@PathVariable Long canchaId) {
        return ResponseEntity.ok(bloqueoService.obtenerBloqueos(canchaId));
    }

    @PostMapping("/recintos/{id}/imagenes")
    public ResponseEntity<?> subirImagenRecinto(
            @PathVariable Long id, 
            @RequestParam("file") MultipartFile archivo) {
        try {
            Imagen imagenGuardada = imagenService.subirImagen(id, archivo);
            return new ResponseEntity<>(imagenGuardada, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al subir imagen: " + e.getMessage());
        }
    }
}
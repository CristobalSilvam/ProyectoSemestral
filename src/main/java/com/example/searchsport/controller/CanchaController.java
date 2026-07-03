package com.example.searchsport.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.searchsport.dto.BloqueDisponibleDTO;
import com.example.searchsport.dto.CanchaRequest;
import com.example.searchsport.entity.Cancha;
import com.example.searchsport.repository.CanchaRepository;
import com.example.searchsport.service.CanchaService;
import com.example.searchsport.service.DisponibilidadService;

import java.util.Map;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/canchas")
public class CanchaController {

    @Autowired
    private CanchaRepository canchaRepository;

    @Autowired
    private DisponibilidadService disponibilidadService;

    // INYECTAMOS TU CANCHA SERVICE AQUÍ
    @Autowired
    private CanchaService canchaService;


    
    // 1. CREAR UNA CANCHA NUEVA 
    @PostMapping
    public ResponseEntity<?> crearCancha(@RequestBody CanchaRequest request) {
        try {
            // Suponiendo que tu método en CanchaService se llama crearCancha o guardarCancha
            Cancha nuevaCancha = canchaService.guardarCancha(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCancha);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear la cancha: " + e.getMessage());
        }
    }

    // 2. SUBIR IMAGEN DE LA CANCHA 
    @PostMapping("/{id}/imagenes")
    public ResponseEntity<?> subirImagenCancha(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            // Aquí en el futuro tu compañero conectará la lógica de AWS S3 o guardado local.
            // Por ahora, le devolvemos un OK al frontend para que termine el flujo con éxito.
            System.out.println("Imagen recibida para la cancha ID: " + id + ". Archivo: " + file.getOriginalFilename());
            return ResponseEntity.ok().body("{\"mensaje\": \"Imagen subida con éxito\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al subir la imagen: " + e.getMessage());
        }
    }


    // MÉTODOS GET 
    // GET /api/canchas
    @GetMapping
    public ResponseEntity<List<Cancha>> listarCanchas() {
        List<Cancha> canchas = canchaRepository.findAll();
        return ResponseEntity.ok(canchas);
    }

    // GET /api/canchas/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Cancha> obtenerCanchaPorId(@PathVariable Long id) {
        return canchaRepository.findById(id)
                .map(cancha -> ResponseEntity.ok(cancha))
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/canchas/{id}/disponibilidad?fecha=YYYY-MM-DD
    @GetMapping("/{id}/disponibilidad")
    public ResponseEntity<List<BloqueDisponibleDTO>> consultarDisponibilidad(
            @PathVariable("id") Long canchaId,
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        List<BloqueDisponibleDTO> disponibles = disponibilidadService.obtenerBloquesDisponibles(canchaId, fecha);
        return ResponseEntity.ok(disponibles);
    }
    @PatchMapping("/{id}/precio")
    public ResponseEntity<?> actualizarPrecioCancha(
            @PathVariable Long id, 
            @RequestBody Map<String, Double> payload) {
        
        Double nuevoPrecio = payload.get("precio");
        if (nuevoPrecio == null) {
            return ResponseEntity.badRequest().body("El campo 'precio' es obligatorio");
        }

        return canchaRepository.findById(id).map(cancha -> {
            cancha.setPrecio(nuevoPrecio);
            canchaRepository.save(cancha);
            return ResponseEntity.ok(cancha);
        }).orElse(ResponseEntity.notFound().build());
    }
}
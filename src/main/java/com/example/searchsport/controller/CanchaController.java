package com.example.searchsport.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.searchsport.dto.BloqueDisponibleDTO;
import com.example.searchsport.dto.CanchaRequest;
import com.example.searchsport.entity.Cancha;
import com.example.searchsport.repository.CanchaRepository;
import com.example.searchsport.service.CanchaService;
import com.example.searchsport.service.DisponibilidadService;

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
    @GetMapping
    public ResponseEntity<List<Cancha>> listarCanchas() {
        List<Cancha> canchas = canchaRepository.findAll();
        return ResponseEntity.ok(canchas);
    }

    // GET /api/canchas/recinto/{recintoId}
    @GetMapping("/recinto/{recintoId}")
    public ResponseEntity<List<Cancha>> listarCanchasPorRecinto(@PathVariable Long recintoId) {
        List<Cancha> canchas = canchaRepository.findByRecintoId(recintoId);
        return ResponseEntity.ok(canchas);
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

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarCancha(@PathVariable Long id) {
        canchaRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

}
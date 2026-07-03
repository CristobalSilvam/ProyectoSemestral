package com.example.searchsport.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.searchsport.dto.ImagenResponseDTO;
import com.example.searchsport.entity.Imagen;
import com.example.searchsport.service.ImagenService;

@RestController
@RequestMapping("/api/recintos")
public class ImagenController {

    @Autowired
    private ImagenService imagenService;

    @PostMapping("/{id}/imagenes")
    public ResponseEntity<?> subirImagenRecinto(
            @PathVariable("id") Long recintoId,
            @RequestParam("file") MultipartFile file) {
        
        try {
            Imagen imagenGuardada = imagenService.subirImagen(recintoId, file);
            
            // Devolvemos un JSON con la URL generada
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Imagen subida con éxito");
            respuesta.put("url", imagenGuardada.getUrl());
            
            return ResponseEntity.ok(respuesta);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "No se pudo subir la imagen: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    @GetMapping("/{id}/imagenes")
    public ResponseEntity<List<ImagenResponseDTO>> listarImagenesRecinto(
            @PathVariable("id") Long recintoId) {
        List<ImagenResponseDTO> imagenes = imagenService.obtenerImagenesPorRecinto(recintoId);
        return ResponseEntity.ok(imagenes);
    }

    @DeleteMapping("/imagenes/{idImagen}")
    public ResponseEntity<?> eliminarImagen(@PathVariable Long idImagen) {
        try {
            imagenService.eliminarImagen(idImagen);
            return ResponseEntity.ok().body("Imagen eliminada correctamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al eliminar la imagen: " + e.getMessage());
        }
    }

}
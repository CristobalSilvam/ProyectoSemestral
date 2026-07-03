package com.example.searchsport.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.searchsport.entity.Imagen;
import com.example.searchsport.service.ImagenService;

import java.util.HashMap;
import java.util.Map;

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
}
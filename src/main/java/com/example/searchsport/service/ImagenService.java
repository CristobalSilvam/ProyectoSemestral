package com.example.searchsport.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.searchsport.entity.Imagen;
import com.example.searchsport.entity.Recinto;
import com.example.searchsport.repository.ImagenRepository;
import com.example.searchsport.repository.RecintoRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImagenService {

    @Autowired
    private ImagenRepository imagenRepository;

    @Autowired
    private RecintoRepository recintoRepository;

    // Carpeta donde se guardarán físicamente las fotos
    private final String UPLOAD_DIR = "uploads/";

    public Imagen subirImagen(Long recintoId, MultipartFile archivo) throws IOException {
        
        // 1. Validar que el recinto exista
        Recinto recinto = recintoRepository.findById(recintoId)
                .orElseThrow(() -> new RuntimeException("Recinto no encontrado"));

        // 2. Validar que el archivo sea una imagen (jpg o png)
        String contentType = archivo.getContentType();
        if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
            throw new RuntimeException("Solo se permiten archivos JPG o PNG");
        }

        // 3. Crear la carpeta "uploads" si no existe en tu computador
        Path rutaDirectorio = Paths.get(UPLOAD_DIR);
        if (!Files.exists(rutaDirectorio)) {
            Files.createDirectories(rutaDirectorio);
        }

        // 4. Generar un nombre único para el archivo
        String nombreOriginal = archivo.getOriginalFilename();
        if (nombreOriginal == null || !nombreOriginal.contains(".")) {
            nombreOriginal = "imagen.jpg"; // Nombre por defecto si viene vacío
        }
        String extension = nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
        String nombreUnico = UUID.randomUUID().toString() + extension;

        // 5. Guardar el archivo físicamente en el disco duro
        Path rutaArchivo = rutaDirectorio.resolve(nombreUnico);
        Files.copy(archivo.getInputStream(), rutaArchivo);

        // 6. Guardar la URL en la base de datos (MySQL)
        Imagen nuevaImagen = new Imagen();
        nuevaImagen.setUrl("/uploads/" + nombreUnico);
        nuevaImagen.setRecinto(recinto);

        return imagenRepository.save(nuevaImagen);
    }
}
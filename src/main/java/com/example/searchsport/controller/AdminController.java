package com.example.searchsport.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.example.searchsport.dto.BloqueoRequest;
import com.example.searchsport.dto.CanchaRequest;
import com.example.searchsport.dto.RecintoRequest;
import com.example.searchsport.dto.ReporteIngresosDTO;
import com.example.searchsport.dto.TarifaRequest;
import com.example.searchsport.entity.Cancha;
import com.example.searchsport.entity.HorarioEspecial;
import com.example.searchsport.entity.Imagen;
import com.example.searchsport.entity.Recinto;
import com.example.searchsport.entity.Rol;
import com.example.searchsport.entity.Tarifa;
import com.example.searchsport.entity.Usuario;
import com.example.searchsport.repository.RolRepository;
import com.example.searchsport.repository.UsuarioRepository;
import com.example.searchsport.service.BloqueoService;
import com.example.searchsport.service.CanchaService;
import com.example.searchsport.service.ImagenService;
import com.example.searchsport.service.RecintoService;
import com.example.searchsport.service.ReporteService;
import com.example.searchsport.service.TarifaService;


@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private RecintoService recintoService;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

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

    @Autowired
    private RolRepository rolRepository;

    @PostMapping("/recintos")
    public ResponseEntity<Recinto> crearRecinto(@RequestBody RecintoRequest request) {
        Recinto nuevoRecinto = recintoService.crearRecinto(request);
        return new ResponseEntity<>(nuevoRecinto, HttpStatus.CREATED);
    }

    @GetMapping("/recintos-lista")
    public ResponseEntity<List<Recinto>> listarTodosLosRecintosAdmin() {
        // Usamos el método que ya tienes en RecintoService
        return ResponseEntity.ok(recintoService.obtenerTodos());
    }

    @GetMapping("/usuarios")
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return ResponseEntity.ok(usuarios);
    }

    // Eliminar Usuario
    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        if (!usuarioRepository.existsById(id)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Usuario no encontrado"));
        }
        usuarioRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Usuario eliminado exitosamente"));
    }

    // Modificar Rol de Usuario
    @PatchMapping("/usuarios/{id}/rol")
    public ResponseEntity<?> cambiarRolUsuario(@PathVariable Long id, @RequestParam Long idRol) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Rol nuevoRol = rolRepository.findById(idRol)
            .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        
        usuario.setRol(nuevoRol);
        usuarioRepository.save(usuario);
        
        return ResponseEntity.ok(Map.of(
            "message", "Rol actualizado exitosamente",
            "nuevoRol", nuevoRol.getNombre()
        ));
    }

    // Activar/Desactivar un usuario (Bloqueo)
    @PatchMapping("/usuarios/{id}/estado")
    public ResponseEntity<?> toggleEstadoUsuario(@PathVariable Long id) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Obtenemos el estado actual. Si por alguna razón es null en la BD, asumimos que es true (activo)
        boolean estadoActual = usuario.getActivo() != null ? usuario.getActivo() : true;
        
        // Cambia el estado actual al opuesto
        usuario.setActivo(!estadoActual);
        usuarioRepository.save(usuario);
        
        return ResponseEntity.ok(Map.of(
            "message", "Estado de usuario actualizado",
            "activo", usuario.getActivo()
        ));
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
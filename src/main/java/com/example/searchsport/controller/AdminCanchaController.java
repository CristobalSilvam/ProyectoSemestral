package com.example.searchsport.controller;

import com.example.searchsport.entity.Cancha;
import com.example.searchsport.entity.Deporte;
import com.example.searchsport.entity.Recinto;
import com.example.searchsport.repository.CanchaRepository;
import com.example.searchsport.repository.DeporteRepository;
import com.example.searchsport.repository.RecintoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/canchas")
public class AdminCanchaController {

    private final CanchaRepository canchaRepository;
    private final RecintoRepository recintoRepository;
    private final DeporteRepository deporteRepository;

    public AdminCanchaController(
            CanchaRepository canchaRepository,
            RecintoRepository recintoRepository,
            DeporteRepository deporteRepository
    ) {
        this.canchaRepository = canchaRepository;
        this.recintoRepository = recintoRepository;
        this.deporteRepository = deporteRepository;
    }

    @GetMapping
    public ResponseEntity<List<CanchaResponse>> listarCanchas() {
        List<CanchaResponse> canchas = canchaRepository.findAll()
                .stream()
                .map(CanchaResponse::new)
                .toList();

        return ResponseEntity.ok(canchas);
    }

    @GetMapping("/opciones")
    public ResponseEntity<CanchaOpcionesResponse> obtenerOpciones() {
        List<RecintoOption> recintos = recintoRepository.findAll()
                .stream()
                .map(RecintoOption::new)
                .toList();

        List<DeporteOption> deportes = deporteRepository.findAll()
                .stream()
                .map(DeporteOption::new)
                .toList();

        return ResponseEntity.ok(new CanchaOpcionesResponse(recintos, deportes));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CanchaResponse> actualizarCancha(
            @PathVariable Long id,
            @RequestBody CanchaUpdateRequest request
    ) {
        Cancha cancha = canchaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cancha no encontrada"));

        Recinto recinto = recintoRepository.findById(request.getRecintoId())
                .orElseThrow(() -> new RuntimeException("Recinto no encontrado"));

        Deporte deporte = deporteRepository.findById(request.getDeporteId())
                .orElseThrow(() -> new RuntimeException("Deporte no encontrado"));

        cancha.setNombreInterno(request.getNombreInterno());
        cancha.setEsTechada(Boolean.TRUE.equals(request.getEsTechada()));
        cancha.setTipoSuperficie(request.getTipoSuperficie());
        cancha.setRecinto(recinto);
        cancha.setDeporte(deporte);

        Cancha actualizada = canchaRepository.save(cancha);

        return ResponseEntity.ok(new CanchaResponse(actualizada));
    }

    public static class CanchaUpdateRequest {
        private String nombreInterno;
        private Boolean esTechada;
        private String tipoSuperficie;
        private Long recintoId;
        private Integer deporteId;

        public String getNombreInterno() {
            return nombreInterno;
        }

        public void setNombreInterno(String nombreInterno) {
            this.nombreInterno = nombreInterno;
        }

        public Boolean getEsTechada() {
            return esTechada;
        }

        public void setEsTechada(Boolean esTechada) {
            this.esTechada = esTechada;
        }

        public String getTipoSuperficie() {
            return tipoSuperficie;
        }

        public void setTipoSuperficie(String tipoSuperficie) {
            this.tipoSuperficie = tipoSuperficie;
        }

        public Long getRecintoId() {
            return recintoId;
        }

        public void setRecintoId(Long recintoId) {
            this.recintoId = recintoId;
        }

        public Integer getDeporteId() {
            return deporteId;
        }

        public void setDeporteId(Integer deporteId) {
            this.deporteId = deporteId;
        }
    }

    public static class CanchaResponse {
        private Long id;
        private String nombreInterno;
        private Boolean esTechada;
        private String tipoSuperficie;

        private Long recintoId;
        private String recintoNombre;
        private Boolean recintoAprobado;

        private Number deporteId;
        private String deporteNombre;

        public CanchaResponse(Cancha cancha) {
            this.id = cancha.getIdCancha();
            this.nombreInterno = cancha.getNombreInterno();
            this.esTechada = Boolean.TRUE.equals(cancha.getEsTechada());
            this.tipoSuperficie = cancha.getTipoSuperficie();

            if (cancha.getRecinto() != null) {
                this.recintoId = cancha.getRecinto().getId();
                this.recintoNombre = cancha.getRecinto().getNombre();
                this.recintoAprobado = Boolean.TRUE.equals(cancha.getRecinto().getAprobado());
            }

            if (cancha.getDeporte() != null) {
                this.deporteId = cancha.getDeporte().getIdDeporte();
                this.deporteNombre = cancha.getDeporte().getNombre();
            }
        }

        public Long getId() {
            return id;
        }

        public String getNombreInterno() {
            return nombreInterno;
        }

        public Boolean getEsTechada() {
            return esTechada;
        }

        public String getTipoSuperficie() {
            return tipoSuperficie;
        }

        public Long getRecintoId() {
            return recintoId;
        }

        public String getRecintoNombre() {
            return recintoNombre;
        }

        public Boolean getRecintoAprobado() {
            return recintoAprobado;
        }

        public Number getDeporteId() {
            return deporteId;
        }

        public String getDeporteNombre() {
            return deporteNombre;
        }
    }

    public static class RecintoOption {
        private Long id;
        private String nombre;
        private Boolean aprobado;

        public RecintoOption(Recinto recinto) {
            this.id = recinto.getId();
            this.nombre = recinto.getNombre();
            this.aprobado = Boolean.TRUE.equals(recinto.getAprobado());
        }

        public Long getId() {
            return id;
        }

        public String getNombre() {
            return nombre;
        }

        public Boolean getAprobado() {
            return aprobado;
        }
    }

    public static class DeporteOption {
        private Number id;
        private String nombre;

        public DeporteOption(Deporte deporte) {
            this.id = deporte.getIdDeporte();
            this.nombre = deporte.getNombre();
        }

        public Number getId() {
            return id;
        }

        public String getNombre() {
            return nombre;
        }
    }

    public static class CanchaOpcionesResponse {
        private List<RecintoOption> recintos;
        private List<DeporteOption> deportes;

        public CanchaOpcionesResponse(
                List<RecintoOption> recintos,
                List<DeporteOption> deportes
        ) {
            this.recintos = recintos;
            this.deportes = deportes;
        }

        public List<RecintoOption> getRecintos() {
            return recintos;
        }

        public List<DeporteOption> getDeportes() {
            return deportes;
        }
    }
}
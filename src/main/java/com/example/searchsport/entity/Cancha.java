package com.example.searchsport.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "CANCHA")
@Data
public class Cancha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cancha")
    private Long idCancha;

    @Column(name = "nombre_interno", nullable = false, length = 50)
    private String nombreInterno;

    @Column(name = "es_techada")
    private Boolean esTechada = false;

    @Column(name = "tipo_superficie", length = 30)
    private String tipoSuperficie;

    @ManyToOne
    @JoinColumn(name = "recinto_id", nullable = false)
    @JsonIgnoreProperties({"canchas"})
    private Recinto recinto;

    @ManyToOne
    @JoinColumn(name = "deporte_id", nullable = false)
    private Deporte deporte;

    @Column(name = "precio")
    private Double precio;
    
    public Double getPrecio() {
        return precio;
    }
    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public void setNombreInterno(String nombreInterno) { this.nombreInterno = nombreInterno; }
    public void setEsTechada(Boolean esTechada) { this.esTechada = esTechada; }
    public void setTipoSuperficie(String tipoSuperficie) { this.tipoSuperficie = tipoSuperficie; }
    public void setRecinto(Recinto recinto) { this.recinto = recinto; }
    public void setDeporte(Deporte deporte) { this.deporte = deporte; }

}
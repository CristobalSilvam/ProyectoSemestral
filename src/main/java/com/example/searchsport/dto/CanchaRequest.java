package com.example.searchsport.dto;

public class CanchaRequest {
    private String nombreInterno;
    private Boolean esTechada;
    private String tipoSuperficie;
    private Long recintoId;
    private Long deporteId;
    private Double precio;

    // --- GETTERS Y SETTERS MANUALES ---

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

    public Long getDeporteId() {
        return deporteId;
    }

    public void setDeporteId(Long deporteId) {
        this.deporteId = deporteId;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }
}
package com.example.searchsport.dto;

import java.math.BigDecimal;

public class MercadoPagoPreferenceRequest {

    private Long reservaId;
    private String titulo;
    private BigDecimal precio;

    public MercadoPagoPreferenceRequest() {
    }

    public MercadoPagoPreferenceRequest(Long reservaId, String titulo, BigDecimal precio) {
        this.reservaId = reservaId;
        this.titulo = titulo;
        this.precio = precio;
    }

    public Long getReservaId() {
        return reservaId;
    }

    public void setReservaId(Long reservaId) {
        this.reservaId = reservaId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
}
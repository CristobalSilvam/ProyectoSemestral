package com.example.searchsport.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.searchsport.dto.ReporteIngresosDTO;
import com.example.searchsport.repository.ReservaRepository;

import java.math.BigDecimal;

@Service
public class ReporteService {

    @Autowired
    private ReservaRepository reservaRepository;

    public ReporteIngresosDTO generarReporteMensual(Long recintoId, int mes, int anio) {
        
        Long cantidadReservas = reservaRepository.contarReservasPagadasPorMesYRecinto(recintoId, mes, anio);
        BigDecimal ingresosTotales = reservaRepository.calcularIngresosPorMesYRecinto(recintoId, mes, anio);

        // Si no hubo ventas en ese mes, la suma de SQL devuelve null. Lo manejamos:
        if (ingresosTotales == null) {
            ingresosTotales = BigDecimal.ZERO;
        }

        return new ReporteIngresosDTO(recintoId, mes, anio, cantidadReservas, ingresosTotales);
    }
}
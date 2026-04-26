package com.example.searchsport.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.searchsport.dto.TarifaRequest;
import com.example.searchsport.entity.Cancha;
import com.example.searchsport.entity.Tarifa;
import com.example.searchsport.repository.TarifaRepository;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

@Service
public class TarifaService {

    @Autowired
    private TarifaRepository tarifaRepository;

    // Guarda una nueva regla de precio definida por el admin [cite: 333]
    public Tarifa guardarTarifa(TarifaRequest request) {
        Tarifa tarifa = new Tarifa();
        
        Cancha cancha = new Cancha();
        cancha.setIdCancha(request.getCanchaId());
        
        tarifa.setCancha(cancha);
        tarifa.setDiaSemana(request.getDiaSemana());
        tarifa.setHoraInicio(request.getHoraInicio());
        tarifa.setHoraFin(request.getHoraFin());
        tarifa.setPrecio(request.getPrecio());

        return tarifaRepository.save(tarifa);
    }

    // Lógica crucial: Encuentra el precio para un bloque horario específico
    public BigDecimal calcularPrecio(Long canchaId, int diaSemana, LocalTime hora) {
        List<Tarifa> tarifas = tarifaRepository.findByCanchaIdCancha(canchaId);
        
        // Buscamos la tarifa que coincida con el día y el rango horario [cite: 38]
        return tarifas.stream()
            .filter(t -> t.getDiaSemana() == diaSemana)
            .filter(t -> !hora.isBefore(t.getHoraInicio()) && hora.isBefore(t.getHoraFin()))
            .map(Tarifa::getPrecio)
            .findFirst()
            .orElse(new BigDecimal("0.00")); // O un precio base por defecto
    }
}
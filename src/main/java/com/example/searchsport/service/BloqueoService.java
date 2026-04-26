package com.example.searchsport.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.searchsport.dto.BloqueoRequest;
import com.example.searchsport.entity.Cancha;
import com.example.searchsport.entity.HorarioEspecial;
import com.example.searchsport.repository.HorarioEspecialRepository;

import java.util.List;

@Service
public class BloqueoService {

    @Autowired
    private HorarioEspecialRepository horarioEspecialRepository;

    public HorarioEspecial crearBloqueo(BloqueoRequest request) {
        HorarioEspecial bloqueo = new HorarioEspecial();
        
        bloqueo.setFecha(request.getFecha());
        bloqueo.setMotivo(request.getMotivo());
        bloqueo.setEstaBloqueado(true); // Siempre true al crearlo

        Cancha cancha = new Cancha();
        cancha.setIdCancha(request.getCanchaId());
        bloqueo.setCancha(cancha);

        return horarioEspecialRepository.save(bloqueo);
    }

    public List<HorarioEspecial> obtenerBloqueos(Long canchaId) {
        return horarioEspecialRepository.findByCanchaIdCancha(canchaId);
    }
}
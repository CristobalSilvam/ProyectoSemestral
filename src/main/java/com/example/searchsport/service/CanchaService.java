package com.example.searchsport.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.searchsport.dto.CanchaRequest;
import com.example.searchsport.entity.Cancha;
import com.example.searchsport.entity.Deporte;
import com.example.searchsport.entity.Recinto;
import com.example.searchsport.repository.CanchaRepository;


@Service
public class CanchaService {
    @Autowired
    private CanchaRepository canchaRepository;

    public Cancha guardarCancha(CanchaRequest request) {
        Cancha cancha = new Cancha();
        cancha.setNombreInterno(request.getNombreInterno());
        cancha.setEsTechada(request.getEsTechada());
        cancha.setTipoSuperficie(request.getTipoSuperficie());

        Recinto r = new Recinto();
        r.setId(request.getRecintoId());
        cancha.setRecinto(r);

        Deporte d = new Deporte();
        d.setIdDeporte(request.getDeporteId());
        cancha.setDeporte(d);

        return canchaRepository.save(cancha);
    }
}
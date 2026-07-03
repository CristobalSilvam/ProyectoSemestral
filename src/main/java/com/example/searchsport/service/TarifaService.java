package com.example.searchsport.service;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.searchsport.dto.TarifaRequest;
import com.example.searchsport.entity.Cancha;
import com.example.searchsport.entity.Tarifa;
import com.example.searchsport.repository.CanchaRepository;
import com.example.searchsport.repository.TarifaRepository;

@Service
public class TarifaService {

    private static final BigDecimal PRECIO_CERO = new BigDecimal("0.00");
    private static final long CACHE_TTL_MS = 60_000;

    @Autowired
    private TarifaRepository tarifaRepository;

    @Autowired(required = false)
    private CanchaRepository canchaRepository;

    private final Map<Long, CacheValue<List<Tarifa>>> tarifasCache = new ConcurrentHashMap<>();
    private final Map<Long, CacheValue<BigDecimal>> precioBaseCache = new ConcurrentHashMap<>();

    public Tarifa guardarTarifa(TarifaRequest request) {
        Tarifa tarifa = new Tarifa();

        Cancha cancha = new Cancha();
        cancha.setIdCancha(request.getCanchaId());

        tarifa.setCancha(cancha);
        tarifa.setDiaSemana(request.getDiaSemana());
        tarifa.setHoraInicio(request.getHoraInicio());
        tarifa.setHoraFin(request.getHoraFin());
        tarifa.setPrecio(request.getPrecio());

        Tarifa tarifaGuardada = tarifaRepository.save(tarifa);

        limpiarCacheCancha(request.getCanchaId());

        return tarifaGuardada;
    }

    public BigDecimal calcularPrecio(Long canchaId, int diaSemana, LocalTime hora) {
        if (canchaId == null || hora == null) {
            return PRECIO_CERO;
        }

        List<Tarifa> tarifasCancha = obtenerTarifasPorCancha(canchaId);
        BigDecimal precioBaseCancha = obtenerPrecioBaseCancha(canchaId);

        BigDecimal precioTarifa = buscarPrecioPorTarifa(tarifasCancha, diaSemana, hora);

        if (esPrecioValido(precioTarifa)) {
            return precioTarifa;
        }

        if (esPrecioValido(precioBaseCancha)) {
            return precioBaseCancha;
        }

        return PRECIO_CERO;
    }

    public List<Tarifa> obtenerTarifasPorCancha(Long canchaId) {
        if (canchaId == null || tarifaRepository == null) {
            return List.of();
        }

        CacheValue<List<Tarifa>> cache = tarifasCache.get(canchaId);

        if (cache != null && !cache.estaVencido()) {
            return cache.getValue();
        }

        List<Tarifa> tarifas = tarifaRepository.findByCanchaIdCancha(canchaId);

        if (tarifas == null) {
            tarifas = List.of();
        }

        tarifasCache.put(canchaId, new CacheValue<>(tarifas));

        return tarifas;
    }

    public BigDecimal obtenerPrecioBaseCancha(Long canchaId) {
        if (canchaId == null || canchaRepository == null) {
            return PRECIO_CERO;
        }

        CacheValue<BigDecimal> cache = precioBaseCache.get(canchaId);

        if (cache != null && !cache.estaVencido()) {
            return cache.getValue();
        }

        BigDecimal precioBase = canchaRepository.findById(canchaId)
                .map(Cancha::getPrecio)
                .filter(this::esPrecioDoubleValido)
                .map(BigDecimal::valueOf)
                .filter(this::esPrecioValido)
                .orElse(PRECIO_CERO);

        precioBaseCache.put(canchaId, new CacheValue<>(precioBase));

        return precioBase;
    }

    public BigDecimal calcularPrecioDesdeDatos(
            List<Tarifa> tarifasCancha,
            BigDecimal precioBaseCancha,
            int diaSemana,
            LocalTime hora
    ) {
        if (hora == null) {
            return PRECIO_CERO;
        }

        BigDecimal precioTarifa = buscarPrecioPorTarifa(tarifasCancha, diaSemana, hora);

        if (esPrecioValido(precioTarifa)) {
            return precioTarifa;
        }

        if (esPrecioValido(precioBaseCancha)) {
            return precioBaseCancha;
        }

        return PRECIO_CERO;
    }

    public void limpiarCacheCancha(Long canchaId) {
        if (canchaId == null) {
            return;
        }

        tarifasCache.remove(canchaId);
        precioBaseCache.remove(canchaId);
    }

    private BigDecimal buscarPrecioPorTarifa(
            List<Tarifa> tarifasCancha,
            int diaSemana,
            LocalTime hora
    ) {
        if (tarifasCancha == null || tarifasCancha.isEmpty()) {
            return PRECIO_CERO;
        }

        return tarifasCancha.stream()
                .filter(tarifa -> tarifa != null)
                .filter(tarifa -> tarifa.getDiaSemana() == diaSemana)
                .filter(tarifa -> tarifa.getHoraInicio() != null)
                .filter(tarifa -> tarifa.getHoraFin() != null)
                .filter(tarifa -> !hora.isBefore(tarifa.getHoraInicio()))
                .filter(tarifa -> hora.isBefore(tarifa.getHoraFin()))
                .map(Tarifa::getPrecio)
                .filter(this::esPrecioValido)
                .findFirst()
                .orElse(PRECIO_CERO);
    }

    private boolean esPrecioValido(BigDecimal precio) {
        return precio != null && precio.compareTo(BigDecimal.ZERO) > 0;
    }

    private boolean esPrecioDoubleValido(Double precio) {
        return precio != null && Double.isFinite(precio) && precio > 0;
    }

    private static class CacheValue<T> {

        private final T value;
        private final long createdAt;

        private CacheValue(T value) {
            this.value = value;
            this.createdAt = System.currentTimeMillis();
        }

        private T getValue() {
            return value;
        }

        private boolean estaVencido() {
            return System.currentTimeMillis() - createdAt > CACHE_TTL_MS;
        }
    }
}
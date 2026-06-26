package com.example.searchsport.service;

import com.example.searchsport.dto.TarifaRequest;
import com.example.searchsport.entity.Tarifa;
import com.example.searchsport.repository.TarifaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TarifaServiceTest {

    @Mock
    private TarifaRepository tarifaRepository;

    @InjectMocks
    private TarifaService tarifaService;

    @Test
    void guardarTarifa_debeGuardarTarifaCorrectamente() {
        TarifaRequest request = new TarifaRequest();
        request.setCanchaId(1L);
        request.setDiaSemana((byte) 5);
        request.setHoraInicio(LocalTime.of(18, 0));
        request.setHoraFin(LocalTime.of(22, 0));
        request.setPrecio(new BigDecimal("30000"));

        when(tarifaRepository.save(any(Tarifa.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Tarifa resultado = tarifaService.guardarTarifa(request);

        assertNotNull(resultado);
        assertNotNull(resultado.getCancha());
        assertEquals(1L, resultado.getCancha().getIdCancha());
        assertEquals(Byte.valueOf((byte) 5), resultado.getDiaSemana());
        assertEquals(LocalTime.of(18, 0), resultado.getHoraInicio());
        assertEquals(LocalTime.of(22, 0), resultado.getHoraFin());
        assertEquals(new BigDecimal("30000"), resultado.getPrecio());

        verify(tarifaRepository, times(1)).save(any(Tarifa.class));
    }

    @Test
    void calcularPrecio_debeRetornarPrecioCuandoCoincideDiaYHora() {
        Long canchaId = 1L;

        Tarifa tarifa = new Tarifa();
        tarifa.setDiaSemana((byte) 5);
        tarifa.setHoraInicio(LocalTime.of(18, 0));
        tarifa.setHoraFin(LocalTime.of(22, 0));
        tarifa.setPrecio(new BigDecimal("30000"));

        when(tarifaRepository.findByCanchaIdCancha(canchaId))
                .thenReturn(List.of(tarifa));

        BigDecimal precio = tarifaService.calcularPrecio(
                canchaId,
                5,
                LocalTime.of(19, 0)
        );

        assertEquals(new BigDecimal("30000"), precio);

        verify(tarifaRepository, times(1)).findByCanchaIdCancha(canchaId);
    }

    @Test
    void calcularPrecio_debeRetornarPrecioSiHoraEsIgualAHoraInicio() {
        Long canchaId = 1L;

        Tarifa tarifa = new Tarifa();
        tarifa.setDiaSemana((byte) 5);
        tarifa.setHoraInicio(LocalTime.of(18, 0));
        tarifa.setHoraFin(LocalTime.of(22, 0));
        tarifa.setPrecio(new BigDecimal("30000"));

        when(tarifaRepository.findByCanchaIdCancha(canchaId))
                .thenReturn(List.of(tarifa));

        BigDecimal precio = tarifaService.calcularPrecio(
                canchaId,
                5,
                LocalTime.of(18, 0)
        );

        assertEquals(new BigDecimal("30000"), precio);

        verify(tarifaRepository, times(1)).findByCanchaIdCancha(canchaId);
    }

    @Test
    void calcularPrecio_debeRetornarCeroSiDiaNoCoincide() {
        Long canchaId = 1L;

        Tarifa tarifa = new Tarifa();
        tarifa.setDiaSemana((byte) 5);
        tarifa.setHoraInicio(LocalTime.of(18, 0));
        tarifa.setHoraFin(LocalTime.of(22, 0));
        tarifa.setPrecio(new BigDecimal("30000"));

        when(tarifaRepository.findByCanchaIdCancha(canchaId))
                .thenReturn(List.of(tarifa));

        BigDecimal precio = tarifaService.calcularPrecio(
                canchaId,
                3,
                LocalTime.of(19, 0)
        );

        assertEquals(new BigDecimal("0.00"), precio);

        verify(tarifaRepository, times(1)).findByCanchaIdCancha(canchaId);
    }

    @Test
    void calcularPrecio_debeRetornarCeroSiHoraEsAntesDelRango() {
        Long canchaId = 1L;

        Tarifa tarifa = new Tarifa();
        tarifa.setDiaSemana((byte) 5);
        tarifa.setHoraInicio(LocalTime.of(18, 0));
        tarifa.setHoraFin(LocalTime.of(22, 0));
        tarifa.setPrecio(new BigDecimal("30000"));

        when(tarifaRepository.findByCanchaIdCancha(canchaId))
                .thenReturn(List.of(tarifa));

        BigDecimal precio = tarifaService.calcularPrecio(
                canchaId,
                5,
                LocalTime.of(17, 0)
        );

        assertEquals(new BigDecimal("0.00"), precio);

        verify(tarifaRepository, times(1)).findByCanchaIdCancha(canchaId);
    }

    @Test
    void calcularPrecio_debeRetornarCeroSiHoraEsIgualAHoraFin() {
        Long canchaId = 1L;

        Tarifa tarifa = new Tarifa();
        tarifa.setDiaSemana((byte) 5);
        tarifa.setHoraInicio(LocalTime.of(18, 0));
        tarifa.setHoraFin(LocalTime.of(22, 0));
        tarifa.setPrecio(new BigDecimal("30000"));

        when(tarifaRepository.findByCanchaIdCancha(canchaId))
                .thenReturn(List.of(tarifa));

        BigDecimal precio = tarifaService.calcularPrecio(
                canchaId,
                5,
                LocalTime.of(22, 0)
        );

        assertEquals(new BigDecimal("0.00"), precio);

        verify(tarifaRepository, times(1)).findByCanchaIdCancha(canchaId);
    }

    @Test
    void calcularPrecio_debeRetornarCeroSiNoExistenTarifas() {
        Long canchaId = 1L;

        when(tarifaRepository.findByCanchaIdCancha(canchaId))
                .thenReturn(List.of());

        BigDecimal precio = tarifaService.calcularPrecio(
                canchaId,
                5,
                LocalTime.of(19, 0)
        );

        assertEquals(new BigDecimal("0.00"), precio);

        verify(tarifaRepository, times(1)).findByCanchaIdCancha(canchaId);
    }

    @Test
    void calcularPrecio_debeRetornarPrimeraTarifaQueCoincide() {
        Long canchaId = 1L;

        Tarifa tarifaManana = new Tarifa();
        tarifaManana.setDiaSemana((byte) 5);
        tarifaManana.setHoraInicio(LocalTime.of(9, 0));
        tarifaManana.setHoraFin(LocalTime.of(12, 0));
        tarifaManana.setPrecio(new BigDecimal("20000"));

        Tarifa tarifaTarde = new Tarifa();
        tarifaTarde.setDiaSemana((byte) 5);
        tarifaTarde.setHoraInicio(LocalTime.of(18, 0));
        tarifaTarde.setHoraFin(LocalTime.of(22, 0));
        tarifaTarde.setPrecio(new BigDecimal("30000"));

        when(tarifaRepository.findByCanchaIdCancha(canchaId))
                .thenReturn(List.of(tarifaManana, tarifaTarde));

        BigDecimal precio = tarifaService.calcularPrecio(
                canchaId,
                5,
                LocalTime.of(19, 0)
        );

        assertEquals(new BigDecimal("30000"), precio);

        verify(tarifaRepository, times(1)).findByCanchaIdCancha(canchaId);
    }
}
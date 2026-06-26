package com.example.searchsport.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest; 
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.resources.preference.Preference;

import jakarta.annotation.PostConstruct;

@Service
public class PagoService {

    @Value("${mercadopago.access-token}")
    private String accessToken;

    // Esto inyecta tu token automáticamente al iniciar Spring Boot
    @PostConstruct
    public void init() {
        MercadoPagoConfig.setAccessToken(accessToken);
    }

    public String crearPreferenciaPago(String tituloCancha, BigDecimal precioTotal) {
        try {
            // 1. Crear el ítem que se va a cobrar
            PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                    .title(tituloCancha)
                    .quantity(1)
                    .unitPrice(precioTotal)
                    .currencyId("CLP") // Moneda: Pesos Chilenos
                    .build();

            List<PreferenceItemRequest> items = new ArrayList<>();
            items.add(itemRequest);

            // 2. NUEVO: Configurar hacia dónde vuelve el usuario tras pagar
            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success("http://localhost:3000/pago/exito")
                    .failure("http://localhost:3000/pago/error")
                    .pending("http://localhost:3000/pago/pendiente")
                    .build();

            // 3. Empaquetar el ítem y las URLs en una "Preferencia"
            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(items)
                    .backUrls(backUrls)             //URLs de retorno
                    .autoReturn("approved")         //Redirección automática si se aprueba
                    .build();

            // 4. Comunicarse con la API de Mercado Pago para generar el cobro
            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);

            // Retornamos el ID al frontend para que pueda abrir la ventana de pago
            return preference.getId();

        } catch (Exception e) {
            throw new RuntimeException("Error al conectar con Mercado Pago: " + e.getMessage());
        }
    }
}
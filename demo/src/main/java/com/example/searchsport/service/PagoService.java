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

    @PostConstruct
    public void init() {
        MercadoPagoConfig.setAccessToken(accessToken);
    }

    public String crearPreferenciaPago(String tituloCancha, BigDecimal precioTotal) {
        try {
            // 1. SALVAVIDAS ANTIFRAUDE: Evitar que el título llegue nulo o vacío
            String tituloSeguro = (tituloCancha == null || tituloCancha.trim().isEmpty()) 
                    ? "Reserva de Cancha en SearchSport" 
                    : tituloCancha;

            // 2. Crear el ítem que se va a cobrar
            PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                    .title(tituloSeguro)
                    .quantity(1)
                    .unitPrice(precioTotal)
                    .currencyId("CLP") // Moneda: Pesos Chilenos
                    .build();

            List<PreferenceItemRequest> items = new ArrayList<>();
            items.add(itemRequest);

            // 3. DESCOMENTADO: Configurar hacia dónde vuelve el usuario tras pagar
            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success("http://localhost:3000/pago/exito")
                    .failure("http://localhost:3000/pago/error")
                    .pending("http://localhost:3000/pago/pendiente")
                    .build();

            // 4. DESCOMENTADO: Empaquetar el ítem y las URLs en la "Preferencia"
            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(items)
                    .backUrls(backUrls)             // <-- Ahora MP sabe a dónde redirigir
                    .autoReturn("approved")         // <-- Redirección automática al aprobar
                    .build();

            // 5. Comunicarse con la API de Mercado Pago
            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);

            return preference.getId();

        } catch (com.mercadopago.exceptions.MPApiException e) {
            System.err.println("=== ERROR DE API MERCADO PAGO ===");
            System.err.println("Código HTTP: " + e.getStatusCode());
            System.err.println("Cuerpo del Error: " + e.getApiResponse().getContent());
            System.err.println("=================================");
            
            throw new RuntimeException("Error detallado de Mercado Pago: " + e.getApiResponse().getContent());
        } catch (Exception e) {
            throw new RuntimeException("Error genérico: " + e.getMessage());
        }
    }
}
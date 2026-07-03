package com.example.searchsport.controller;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.searchsport.dto.MercadoPagoPreferenceRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/mercadopago")
public class MercadoPagoController {

    @Value("${mercadopago.access-token:}")
    private String accessToken;

    @Value("${frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Value("${mercadopago.auto-return:false}")
    private boolean autoReturn;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/crear-preferencia")
    public ResponseEntity<?> crearPreferencia(@RequestBody MercadoPagoPreferenceRequest request) {
        try {
            if (accessToken == null || accessToken.isBlank()) {
                return ResponseEntity.status(500).body(Map.of(
                        "message", "Falta configurar mercadopago.access-token"
                ));
            }

            if (request == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "message", "El body de la solicitud es obligatorio"
                ));
            }

            if (request.getReservaId() == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "message", "Falta reservaId"
                ));
            }

            if (request.getPrecio() == null || request.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest().body(Map.of(
                        "message", "El precio debe ser mayor a 0"
                ));
            }

            String titulo = request.getTitulo();

            if (titulo == null || titulo.isBlank()) {
                titulo = "Reserva SearchSport";
            }

            int precioEntero = request.getPrecio().intValue();

            String frontendBaseUrl = normalizarFrontendUrl();

            Map<String, Object> item = new HashMap<>();
            item.put("title", titulo);
            item.put("quantity", 1);
            item.put("currency_id", "CLP");
            item.put("unit_price", precioEntero);

            Map<String, Object> backUrls = new HashMap<>();
            backUrls.put("success", frontendBaseUrl + "/pago/exito?reservaId=" + request.getReservaId());
            backUrls.put("failure", frontendBaseUrl + "/pago/error?reservaId=" + request.getReservaId());
            backUrls.put("pending", frontendBaseUrl + "/pago/pendiente?reservaId=" + request.getReservaId());

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("reservaId", request.getReservaId());

            Map<String, Object> body = new HashMap<>();
            body.put("items", List.of(item));
            body.put("external_reference", String.valueOf(request.getReservaId()));
            body.put("metadata", metadata);
            body.put("back_urls", backUrls);

            /*
             * En local debe quedar desactivado:
             * mercadopago.auto-return=false
             *
             * En producción con frontend HTTPS público:
             * MERCADOPAGO_AUTO_RETURN=true
             */
            if (autoReturn) {
                body.put("auto_return", "approved");
            }

            String jsonBody = objectMapper.writeValueAsString(body);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.mercadopago.com/checkout/preferences"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + accessToken.trim())
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpClient client = HttpClient.newHttpClient();

            HttpResponse<String> response = client.send(
                    httpRequest,
                    HttpResponse.BodyHandlers.ofString()
            );

            String mercadoPagoBody = response.body();

            System.out.println("STATUS MERCADOPAGO: " + response.statusCode());
            System.out.println("REQUEST ENVIADO A MERCADOPAGO: " + jsonBody);
            System.out.println("BODY MERCADOPAGO: " + mercadoPagoBody);

            Map<String, Object> responseBody = new HashMap<>();

            if (mercadoPagoBody != null && !mercadoPagoBody.isBlank()) {
                responseBody = objectMapper.readValue(
                        mercadoPagoBody,
                        new TypeReference<Map<String, Object>>() {}
                );
            }

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                Map<String, Object> error = new HashMap<>();
                error.put("message", "MercadoPago rechazó la creación de la preferencia");
                error.put("status", response.statusCode());
                error.put("detalle", responseBody);
                error.put("bodyCrudo", mercadoPagoBody);
                error.put("requestEnviado", body);

                return ResponseEntity.status(response.statusCode()).body(error);
            }

            Object id = responseBody.get("id");
            Object initPoint = responseBody.get("init_point");
            Object sandboxInitPoint = responseBody.get("sandbox_init_point");

            if (id == null || initPoint == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("message", "MercadoPago respondió, pero no devolvió preferenceId o init_point");
                error.put("status", response.statusCode());
                error.put("bodyCrudo", mercadoPagoBody);

                return ResponseEntity.status(500).body(error);
            }

            Map<String, Object> resultado = new HashMap<>();
            resultado.put("preferenceId", id);
            resultado.put("id", id);
            resultado.put("init_point", initPoint);
            resultado.put("sandbox_init_point", sandboxInitPoint);

            return ResponseEntity.ok(resultado);

        } catch (Exception e) {
            e.printStackTrace();

            Map<String, Object> error = new HashMap<>();
            error.put("message", "Error interno creando preferencia de MercadoPago");
            error.put("detalle", e.getMessage());

            return ResponseEntity.status(500).body(error);
        }
    }

    private String normalizarFrontendUrl() {
        if (frontendUrl == null || frontendUrl.isBlank()) {
            return "http://localhost:3000";
        }

        String url = frontendUrl.trim();

        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        return url;
    }
}
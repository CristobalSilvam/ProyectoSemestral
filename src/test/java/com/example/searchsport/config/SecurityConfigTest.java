package com.example.searchsport.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

class SecurityConfigTest {

    @Test
    void corsConfigurationSource_debeConfigurarOrigenesMetodosHeadersYCredenciales() {
        SecurityConfig securityConfig = new SecurityConfig();

        CorsConfigurationSource source = securityConfig.corsConfigurationSource();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/recintos");

        CorsConfiguration configuration = source.getCorsConfiguration(request);

        assertNotNull(configuration);
        assertEquals(
                List.of("http://localhost:3000", "https://front-taller.vercel.app"),
                configuration.getAllowedOrigins()
        );
        assertEquals(
                List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"),
                configuration.getAllowedMethods()
        );
        assertEquals(List.of("*"), configuration.getAllowedHeaders());
        assertEquals(true, configuration.getAllowCredentials());
    }

    @Test
    void passwordEncoder_debeCrearBCryptPasswordEncoderValido() {
        SecurityConfig securityConfig = new SecurityConfig();

        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();

        String passwordPlano = "SearchSport123";
        String passwordEncriptado = passwordEncoder.encode(passwordPlano);

        assertNotNull(passwordEncoder);
        assertNotNull(passwordEncriptado);
        assertTrue(passwordEncoder.matches(passwordPlano, passwordEncriptado));
    }

    @Test
    void authenticationManager_debeRetornarAuthenticationManagerDesdeConfiguration() throws Exception {
        SecurityConfig securityConfig = new SecurityConfig();

        AuthenticationConfiguration authenticationConfiguration = mock(AuthenticationConfiguration.class);
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);

        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(authenticationManager);

        AuthenticationManager resultado = securityConfig.authenticationManager(authenticationConfiguration);

        assertNotNull(resultado);
        assertEquals(authenticationManager, resultado);
    }
}
package com.example.searchsport.util;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    private static final String SECRET_BASE64 =
            "MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDE=";

    private static final String OTHER_SECRET_BASE64 =
            "YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXoxMjM0NTY=";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secretKeyProperty", SECRET_BASE64);
    }

    @Test
    void generarToken_debeCrearTokenValido() {
        String token = jwtUtil.generarToken("cliente@email.com");

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertTrue(token.contains("."));
    }

    @Test
    void extraerEmail_debeRetornarEmailDelToken() {
        String token = jwtUtil.generarToken("cliente@email.com");

        String email = jwtUtil.extraerEmail(token);

        assertEquals("cliente@email.com", email);
    }

    @Test
    void validarToken_debeRetornarTrueSiEmailCoincideYTokenNoEstaExpirado() {
        String token = jwtUtil.generarToken("cliente@email.com");

        boolean valido = jwtUtil.validarToken(token, "cliente@email.com");

        assertTrue(valido);
    }

    @Test
    void validarToken_debeRetornarFalseSiEmailNoCoincide() {
        String token = jwtUtil.generarToken("cliente@email.com");

        boolean valido = jwtUtil.validarToken(token, "otro@email.com");

        assertFalse(valido);
    }

    @Test
    void validarToken_debeLanzarExcepcionSiTokenEstaExpirado() {
        String tokenExpirado = generarTokenExpirado("cliente@email.com");

        assertThrows(
                ExpiredJwtException.class,
                () -> jwtUtil.validarToken(tokenExpirado, "cliente@email.com")
        );
    }

    @Test
    void extraerEmail_debeLanzarExcepcionSiTokenEsInvalido() {
        assertThrows(
                JwtException.class,
                () -> jwtUtil.extraerEmail("token-invalido")
        );
    }

    @Test
    void extraerEmail_debeLanzarExcepcionSiTokenFueFirmadoConOtraClave() {
        String tokenConOtraFirma = generarTokenConOtraFirma("cliente@email.com");

        assertThrows(
                JwtException.class,
                () -> jwtUtil.extraerEmail(tokenConOtraFirma)
        );
    }

    private String generarTokenExpirado(String email) {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_BASE64);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        Date ahora = new Date();
        Date expiracionPasada = new Date(ahora.getTime() - 1000 * 60);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(ahora.getTime() - 1000 * 60 * 60))
                .setExpiration(expiracionPasada)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private String generarTokenConOtraFirma(String email) {
        byte[] keyBytes = Decoders.BASE64.decode(OTHER_SECRET_BASE64);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
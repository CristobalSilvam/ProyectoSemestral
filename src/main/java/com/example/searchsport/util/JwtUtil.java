package com.example.searchsport.util;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    // Spring busca "jwt.secret" en application.properties y se inyecta aquí
    @Value("${jwt.secret}")
    private String secretKeyProperty;
    
    // Tiempo de validez del token (ejemplo: 10 horas)
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 10;

    // Genera la Key criptográfica a partir del texto del properties
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKeyProperty);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generarToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extraerEmail(String token) {
        return extraerClaim(token, Claims::getSubject);
    }

    public boolean validarToken(String token, String emailUsuario) {
        final String emailToken = extraerEmail(token);
        return (emailToken.equals(emailUsuario) && !isTokenExpirado(token));
    }

    private boolean isTokenExpirado(String token) {
        return extraerClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extraerClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey()) 
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }
}
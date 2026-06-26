package com.example.searchsport.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyString;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.searchsport.service.CustomUserDetailsService;
import com.example.searchsport.util.JwtUtil;

import jakarta.servlet.FilterChain;

class JwtAuthenticationFilterTest {

    @AfterEach
    void limpiarContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_debeContinuarSiNoHayAuthorizationHeader() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter();
        JwtUtil jwtUtil = Mockito.mock(JwtUtil.class);
        CustomUserDetailsService userDetailsService = Mockito.mock(CustomUserDetailsService.class);
        FilterChain filterChain = Mockito.mock(FilterChain.class);

        inyectarDependencias(filter, jwtUtil, userDetailsService);

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(jwtUtil, never()).extraerEmail(anyString());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_debeContinuarSiAuthorizationNoEsBearer() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter();
        JwtUtil jwtUtil = Mockito.mock(JwtUtil.class);
        CustomUserDetailsService userDetailsService = Mockito.mock(CustomUserDetailsService.class);
        FilterChain filterChain = Mockito.mock(FilterChain.class);

        inyectarDependencias(filter, jwtUtil, userDetailsService);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Basic token");

        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(jwtUtil, never()).extraerEmail(anyString());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_debeAutenticarSiTokenEsValido() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter();
        JwtUtil jwtUtil = Mockito.mock(JwtUtil.class);
        CustomUserDetailsService userDetailsService = Mockito.mock(CustomUserDetailsService.class);
        FilterChain filterChain = Mockito.mock(FilterChain.class);

        inyectarDependencias(filter, jwtUtil, userDetailsService);

        String token = "token-valido";
        String email = "admin@searchsport.cl";

        UserDetails userDetails = new User(
                email,
                "password",
                List.of(new SimpleGrantedAuthority("ADMIN"))
        );

        when(jwtUtil.extraerEmail(token)).thenReturn(email);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
        when(jwtUtil.validarToken(token, email)).thenReturn(true);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);

        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(email, SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Test
    void doFilterInternal_noDebeAutenticarSiTokenNoEsValido() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter();
        JwtUtil jwtUtil = Mockito.mock(JwtUtil.class);
        CustomUserDetailsService userDetailsService = Mockito.mock(CustomUserDetailsService.class);
        FilterChain filterChain = Mockito.mock(FilterChain.class);

        inyectarDependencias(filter, jwtUtil, userDetailsService);

        String token = "token-invalido";
        String email = "admin@searchsport.cl";

        UserDetails userDetails = new User(
                email,
                "password",
                List.of(new SimpleGrantedAuthority("ADMIN"))
        );

        when(jwtUtil.extraerEmail(token)).thenReturn(email);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
        when(jwtUtil.validarToken(token, email)).thenReturn(false);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);

        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_debeRetornarUnauthorizedSiTokenLanzaExcepcion() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter();
        JwtUtil jwtUtil = Mockito.mock(JwtUtil.class);
        CustomUserDetailsService userDetailsService = Mockito.mock(CustomUserDetailsService.class);
        FilterChain filterChain = Mockito.mock(FilterChain.class);

        inyectarDependencias(filter, jwtUtil, userDetailsService);

        String token = "token-malo";

        when(jwtUtil.extraerEmail(token)).thenThrow(new RuntimeException("Token inválido"));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);

        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        assertEquals(401, response.getStatus());
        assertEquals("application/json", response.getContentType());
        assertEquals("{\"error\":\"Token invalido o error al autenticar\"}", response.getContentAsString());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, never()).doFilter(request, response);
    }

    private void inyectarDependencias(
            JwtAuthenticationFilter filter,
            JwtUtil jwtUtil,
            CustomUserDetailsService customUserDetailsService
    ) throws Exception {
        Field jwtUtilField = JwtAuthenticationFilter.class.getDeclaredField("jwtUtil");
        jwtUtilField.setAccessible(true);
        jwtUtilField.set(filter, jwtUtil);

        Field userDetailsServiceField = JwtAuthenticationFilter.class.getDeclaredField("customUserDetailsService");
        userDetailsServiceField.setAccessible(true);
        userDetailsServiceField.set(filter, customUserDetailsService);
    }
}
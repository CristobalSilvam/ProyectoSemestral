package com.example.searchsport.controller;

import com.example.searchsport.dto.AuthResponse;
import com.example.searchsport.dto.LoginRequest;
import com.example.searchsport.dto.RegisterRequest;
import com.example.searchsport.entity.Rol;
import com.example.searchsport.entity.Usuario;
import com.example.searchsport.repository.RolRepository;
import com.example.searchsport.repository.UsuarioRepository;
import com.example.searchsport.util.JwtUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthController authController;

    @AfterEach
    void limpiarSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void registrar_debeRegistrarUsuarioClienteCorrectamente() {
        RegisterRequest request = crearRegisterRequest();
        request.setIdRol(1L);

        Rol rolCliente = new Rol();
        rolCliente.setIdRol(1L);
        rolCliente.setNombre("CLIENTE");

        when(usuarioRepository.findByEmail("cliente@email.com"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("123456"))
                .thenReturn("password-encriptada");

        when(rolRepository.findById(1L))
                .thenReturn(Optional.of(rolCliente));

        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<?> response = authController.registrar(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Usuario registrado exitosamente", response.getBody());

        verify(usuarioRepository, times(1)).findByEmail("cliente@email.com");
        verify(passwordEncoder, times(1)).encode("123456");
        verify(rolRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void registrar_debeRegistrarUsuarioDuenioCorrectamente() {
        RegisterRequest request = crearRegisterRequest();
        request.setIdRol(2L);

        Rol rolDuenio = new Rol();
        rolDuenio.setIdRol(2L);
        rolDuenio.setNombre("DUENIO");

        when(usuarioRepository.findByEmail("cliente@email.com"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("123456"))
                .thenReturn("password-encriptada");

        when(rolRepository.findById(2L))
                .thenReturn(Optional.of(rolDuenio));

        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<?> response = authController.registrar(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Usuario registrado exitosamente", response.getBody());

        verify(usuarioRepository, times(1)).findByEmail("cliente@email.com");
        verify(passwordEncoder, times(1)).encode("123456");
        verify(rolRepository, times(1)).findById(2L);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void registrar_debeRetornarBadRequestSiEmailYaExiste() {
        RegisterRequest request = crearRegisterRequest();
        request.setIdRol(1L);

        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setId(1L);
        usuarioExistente.setEmail("cliente@email.com");

        when(usuarioRepository.findByEmail("cliente@email.com"))
                .thenReturn(Optional.of(usuarioExistente));

        ResponseEntity<?> response = authController.registrar(request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("El email ya está registrado", response.getBody());

        verify(usuarioRepository, times(1)).findByEmail("cliente@email.com");
        verify(passwordEncoder, never()).encode(anyString());
        verify(rolRepository, never()).findById(anyLong());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void registrar_debeRetornarBadRequestSiRolEsNull() {
        RegisterRequest request = crearRegisterRequest();
        request.setIdRol(null);

        when(usuarioRepository.findByEmail("cliente@email.com"))
                .thenReturn(Optional.empty());

        ResponseEntity<?> response = authController.registrar(request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Rol inválido. Solo puedes registrarte como Cliente o Dueño.", response.getBody());

        verify(usuarioRepository, times(1)).findByEmail("cliente@email.com");
        verify(passwordEncoder, never()).encode(anyString());
        verify(rolRepository, never()).findById(anyLong());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void registrar_debeRetornarBadRequestSiIntentaRegistrarseComoAdmin() {
        RegisterRequest request = crearRegisterRequest();
        request.setIdRol(3L);

        when(usuarioRepository.findByEmail("cliente@email.com"))
                .thenReturn(Optional.empty());

        ResponseEntity<?> response = authController.registrar(request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Rol inválido. Solo puedes registrarte como Cliente o Dueño.", response.getBody());

        verify(usuarioRepository, times(1)).findByEmail("cliente@email.com");
        verify(passwordEncoder, never()).encode(anyString());
        verify(rolRepository, never()).findById(anyLong());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void registrar_debeLanzarExcepcionSiRolNoExisteEnBaseDeDatos() {
        RegisterRequest request = crearRegisterRequest();
        request.setIdRol(1L);

        when(usuarioRepository.findByEmail("cliente@email.com"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("123456"))
                .thenReturn("password-encriptada");

        when(rolRepository.findById(1L))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authController.registrar(request)
        );

        assertEquals("Error: Rol no encontrado en la base de datos.", exception.getMessage());

        verify(usuarioRepository, times(1)).findByEmail("cliente@email.com");
        verify(passwordEncoder, times(1)).encode("123456");
        verify(rolRepository, times(1)).findById(1L);
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void login_debeRetornarTokenYRolCuandoCredencialesSonCorrectas() {
        LoginRequest request = new LoginRequest();
        request.setEmail("cliente@email.com");
        request.setPassword("123456");

        Rol rol = new Rol();
        rol.setIdRol(1L);
        rol.setNombre("CLIENTE");

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("cliente@email.com");
        usuario.setRol(rol);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(jwtUtil.generarToken("cliente@email.com"))
                .thenReturn("token-test");

        when(usuarioRepository.findByEmail("cliente@email.com"))
                .thenReturn(Optional.of(usuario));

        ResponseEntity<AuthResponse> response = authController.login(request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(authentication, SecurityContextHolder.getContext().getAuthentication());

        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil, times(1)).generarToken("cliente@email.com");
        verify(usuarioRepository, times(1)).findByEmail("cliente@email.com");
    }

    @Test
    void login_debeLanzarExcepcionSiCredencialesSonIncorrectas() {
        LoginRequest request = new LoginRequest();
        request.setEmail("cliente@email.com");
        request.setPassword("incorrecta");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Credenciales inválidas"));

        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> authController.login(request)
        );

        assertEquals("Credenciales inválidas", exception.getMessage());

        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil, never()).generarToken(anyString());
        verify(usuarioRepository, never()).findByEmail(anyString());
    }

    @Test
    void login_debeLanzarExcepcionSiUsuarioNoExisteDespuesDeAutenticar() {
        LoginRequest request = new LoginRequest();
        request.setEmail("cliente@email.com");
        request.setPassword("123456");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(jwtUtil.generarToken("cliente@email.com"))
                .thenReturn("token-test");

        when(usuarioRepository.findByEmail("cliente@email.com"))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authController.login(request)
        );

        assertEquals("Usuario no encontrado en la base de datos", exception.getMessage());

        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil, times(1)).generarToken("cliente@email.com");
        verify(usuarioRepository, times(1)).findByEmail("cliente@email.com");
    }

    private RegisterRequest crearRegisterRequest() {
        RegisterRequest request = new RegisterRequest();
        request.setRut("12.345.678-9");
        request.setNombre("Cristobal");
        request.setSegundoNombre("Andres");
        request.setApellidoPaterno("Silva");
        request.setApellidoMaterno("Perez");
        request.setEmail("cliente@email.com");
        request.setPassword("123456");
        return request;
    }
}
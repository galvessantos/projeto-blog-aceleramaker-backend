package aceleramaker.project.controller;

import aceleramaker.project.dto.CreateUsuarioDto;
import aceleramaker.project.dto.LoginDto;
import aceleramaker.project.dto.LoginRespostaDto;
import aceleramaker.project.exceptions.BadRequestException;
import aceleramaker.project.security.JwtTokenProvider;
import aceleramaker.project.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import aceleramaker.project.entity.Usuario;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    private AuthenticationManager authenticationManager;
    private JwtTokenProvider jwtTokenProvider;
    private UsuarioService usuarioService;
    private AuthController authController;

    @BeforeEach
    void setUp() {
        authenticationManager = mock(AuthenticationManager.class);
        jwtTokenProvider = mock(JwtTokenProvider.class);
        usuarioService = mock(UsuarioService.class);
        authController = new AuthController(authenticationManager, jwtTokenProvider, usuarioService);
    }

    @Test
    void testLoginSuccess() {
        LoginDto loginDto = new LoginDto("usuario", "senha123");
        Authentication auth = mock(Authentication.class);
        Usuario usuario = mock(Usuario.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(jwtTokenProvider.generateToken(auth)).thenReturn("token123");

        when(auth.getPrincipal()).thenReturn(usuario);
        when(usuario.getId()).thenReturn(1L);
        when(usuario.getNome()).thenReturn("Gabriel");
        when(usuario.getUsername()).thenReturn("usuario");
        when(usuario.getEmail()).thenReturn("usuario@email.com");
        when(usuario.getFoto()).thenReturn("foto.png");

        ResponseEntity<Map<String, Object>> response = authController.login(loginDto);

        assertEquals(200, response.getStatusCode().value());
        Map<String, Object> body = response.getBody();

        assertNotNull(body);
        assertEquals("token123", body.get("token"));
        assertEquals("Bearer", body.get("tipo"));

        Map<String, Object> usuarioData = (Map<String, Object>) body.get("usuario");
        assertNotNull(usuarioData);
        assertEquals(1L, usuarioData.get("id"));
        assertEquals("Gabriel", usuarioData.get("nome"));
        assertEquals("usuario", usuarioData.get("username"));
        assertEquals("usuario@email.com", usuarioData.get("email"));
        assertEquals("foto.png", usuarioData.get("foto"));
    }

    @Test
    void testLoginFailure() {
        LoginDto loginDto = new LoginDto("usuario", "senhaErrada");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Falha"));

        assertThrows(BadRequestException.class, () -> authController.login(loginDto));
    }

    @Test
    void testRegisterSuccess() {
        CreateUsuarioDto dto = new CreateUsuarioDto("João", "joao", "joao@email.com", "senha123");

        when(usuarioService.createUsuario(dto)).thenReturn(1L);

        ResponseEntity<Map<String, String>> response = authController.register(dto);

        assertEquals(201, response.getStatusCode().value());
        assertEquals("Usuário registrado com sucesso!", response.getBody().get("message"));
        assertEquals("/v1/usuarios/1", response.getHeaders().getLocation().toString());
    }

    @Test
    void testRegisterFailure() {
        CreateUsuarioDto dto = new CreateUsuarioDto("João", "joao", "joao@email.com", "senha123");

        when(usuarioService.createUsuario(dto)).thenThrow(new RuntimeException("Erro ao salvar"));

        assertThrows(BadRequestException.class, () -> authController.register(dto));
    }
}

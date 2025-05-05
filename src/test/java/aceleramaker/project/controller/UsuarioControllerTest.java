package aceleramaker.project.controller;

import aceleramaker.project.dto.UpdateUsuarioDto;
import aceleramaker.project.dto.UsuarioRespostaDto;
import aceleramaker.project.entity.Usuario;
import aceleramaker.project.exceptions.ResourceNotFoundException;
import aceleramaker.project.repository.UsuarioRepository;
import aceleramaker.project.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioControllerTest {

    private UsuarioService usuarioService;
    private UsuarioRepository usuarioRepository;
    private PasswordEncoder passwordEncoder;
    private UsuarioController usuarioController;

    @BeforeEach
    void setUp() {
        usuarioService = mock(UsuarioService.class);
        usuarioRepository = mock(UsuarioRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        usuarioController = new UsuarioController(usuarioService, usuarioRepository, passwordEncoder);
    }

    @Test
    void testGetUsuarioById_Found() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("joao");
        usuario.setUsername("joao_user");
        usuario.setEmail("joao@example.com");
        usuario.setFoto(null);
        Instant now = Instant.now();
        usuario.setCreationTimestamp(now);

        when(usuarioService.getUsuarioById(1L)).thenReturn(Optional.of(usuario));

        ResponseEntity<UsuarioRespostaDto> response = usuarioController.getUsuarioById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        UsuarioRespostaDto body = response.getBody();
        assertNotNull(body);
        assertEquals("joao", body.nome());
        assertEquals("joao_user", body.username());
        assertEquals("joao@example.com", body.email());
        assertEquals(null, body.foto());
        assertEquals(now, body.creationTimestamp());
    }

    @Test
    void testGetUsuarioById_NotFound() {
        when(usuarioService.getUsuarioById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> usuarioController.getUsuarioById(1L));
    }

    @Test
    void testListUsuarios() {
        Usuario u1 = new Usuario();
        u1.setNome("u1");
        u1.setUsername("user1");
        u1.setEmail("user1@example.com");
        u1.setFoto("foto1.jpg");
        u1.setCreationTimestamp(Instant.now());

        Usuario u2 = new Usuario();
        u2.setNome("u2");
        u2.setUsername("user2");
        u2.setEmail("user2@example.com");
        u2.setFoto("foto2.jpg");
        u2.setCreationTimestamp(Instant.now());

        when(usuarioService.listUsers()).thenReturn(List.of(u1, u2));

        ResponseEntity<List<UsuarioRespostaDto>> response = usuarioController.listUsuarios();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertEquals("u1", response.getBody().get(0).nome());
        assertEquals("u2", response.getBody().get(1).nome());
    }

    @Test
    void testUpdateUsuario() {
        UpdateUsuarioDto dto = new UpdateUsuarioDto(
                "Novo Nome",
                "novaSenha",
                "foto_nova.jpg"
        );

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("joao");
        usuario.setEmail("joao@example.com");

        UserDetails current = mock(UserDetails.class);
        when(current.getUsername()).thenReturn("joao");

        when(usuarioRepository.findByUsernameOrEmail("joao", "joao")).thenReturn(Optional.of(usuario));
        doNothing().when(usuarioService).updateUsuarioById(eq(1L), any(UpdateUsuarioDto.class));
        when(usuarioService.getUsuarioById(1L)).thenReturn(Optional.of(usuario));

        ResponseEntity<UsuarioRespostaDto> response = usuarioController.atualizarPerfil("Novo Nome", null, current);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(usuarioService).updateUsuarioById(eq(1L), any(UpdateUsuarioDto.class));
    }

    @Test
    void testDeleteById() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("joao");

        UserDetails current = mock(UserDetails.class);
        when(current.getUsername()).thenReturn("joao");

        when(usuarioService.getUsuarioById(1L)).thenReturn(Optional.of(usuario));
        doNothing().when(usuarioService).deleteById(1L);

        ResponseEntity<Void> response = usuarioController.deleteById(1L, current);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testDeleteById_AccessDenied() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("joao");

        UserDetails current = mock(UserDetails.class);
        when(current.getUsername()).thenReturn("maria");

        when(usuarioService.getUsuarioById(1L)).thenReturn(Optional.of(usuario));

        assertThrows(AccessDeniedException.class, () -> usuarioController.deleteById(1L, current));
    }
}
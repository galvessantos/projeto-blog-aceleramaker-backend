package aceleramaker.project.controller;

import aceleramaker.project.dto.UpdateUsuarioDto;
import aceleramaker.project.dto.UsuarioRespostaDto;
import aceleramaker.project.entity.Usuario;
import aceleramaker.project.exceptions.AccessDeniedCustomException;
import aceleramaker.project.exceptions.ResourceNotFoundException;
import aceleramaker.project.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioControllerTest {

    private UsuarioService usuarioService;
    private UsuarioController usuarioController;

    @BeforeEach
    void setUp() {
        usuarioService = mock(UsuarioService.class);
        usuarioController = new UsuarioController(usuarioService);
    }

    @Test
    void testGetUsuarioById_Found() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("joao");
        usuario.setFoto(null);
        Instant now = Instant.now();
        usuario.setCreationTimestamp(now);

        when(usuarioService.getUsuarioById(1L)).thenReturn(Optional.of(usuario));

        ResponseEntity<UsuarioRespostaDto> response = usuarioController.getUsuarioById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        UsuarioRespostaDto body = response.getBody();
        assertNotNull(body);
        assertEquals("joao", body.nome());
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
        u1.setFoto("foto1.jpg");
        u1.setCreationTimestamp(Instant.now());

        Usuario u2 = new Usuario();
        u2.setNome("u2");
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
    void testUpdateUsuarioById() {
        UpdateUsuarioDto dto = new UpdateUsuarioDto(
                "Novo Nome",
                "novoUsername",
                "novaSenha",
                "foto_nova.jpg"
        );

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("joao");

        UserDetails current = mock(UserDetails.class);
        when(current.getUsername()).thenReturn("joao");

        when(usuarioService.getUsuarioById(1L)).thenReturn(Optional.of(usuario));
        doNothing().when(usuarioService).updateUsuarioById(1L, dto);

        ResponseEntity<Void> response = usuarioController.updateUsuarioById(1L, dto, current);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testUpdateUsuarioById_AccessDenied() {
        UpdateUsuarioDto dto = new UpdateUsuarioDto(
                "Novo Nome",
                "novoUsername",
                "novaSenha",
                "foto_nova.jpg"
        );

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("joao");

        UserDetails current = mock(UserDetails.class);
        when(current.getUsername()).thenReturn("maria");

        when(usuarioService.getUsuarioById(1L)).thenReturn(Optional.of(usuario));

        assertThrows(AccessDeniedCustomException.class, () -> usuarioController.updateUsuarioById(1L, dto, current));
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

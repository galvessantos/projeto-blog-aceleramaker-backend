package aceleramaker.project.controller;

import aceleramaker.project.dto.CreateUsuarioDto;
import aceleramaker.project.dto.UpdateUsuarioDto;
import aceleramaker.project.entity.Usuario;
import aceleramaker.project.exceptions.ResourceNotFoundException;
import aceleramaker.project.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioControllerTest {

    private UsuarioService usuarioService;
    private UsuarioController usuarioController;

    @BeforeEach
    void setUp() {
        usuarioService = Mockito.mock(UsuarioService.class);
        usuarioController = new UsuarioController(usuarioService);
    }

    @Test
    void testCreateUser() {
        CreateUsuarioDto dto = new CreateUsuarioDto(
                "João",
                "joao",
                "joao@email.com",
                "12345678"
        );

        when(usuarioService.createUsuario(any(CreateUsuarioDto.class))).thenReturn(1L);

        ResponseEntity<Usuario> response = usuarioController.createUser(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("/v1/usuarios/1", response.getHeaders().getLocation().toString());
    }

    @Test
    void testGetUsuarioById_Found() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("João");

        when(usuarioService.getUsuarioById(1L)).thenReturn(Optional.of(usuario));

        ResponseEntity<Usuario> response = usuarioController.getUsuarioById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(usuario, response.getBody());
    }

    @Test
    void testGetUsuarioById_NotFound() {
        when(usuarioService.getUsuarioById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> usuarioController.getUsuarioById(1L));
    }

    @Test
    void testListUsuarios() {
        Usuario u1 = new Usuario();
        Usuario u2 = new Usuario();
        List<Usuario> lista = List.of(u1, u2);

        when(usuarioService.listUsers()).thenReturn(lista);

        ResponseEntity<List<Usuario>> response = usuarioController.listUsuarios();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(lista, response.getBody());
    }

    @Test
    void testUpdateUsuarioById() {
        UpdateUsuarioDto dto = new UpdateUsuarioDto(
                "Novo Nome",
                "novoUsername",
                "novaSenha",
                "foto_nova.jpg"
        );

        doNothing().when(usuarioService).updateUsuarioById(1L, dto);

        ResponseEntity<Void> response = usuarioController.updateUsuarioById(1L, dto);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testDeleteById() {
        doNothing().when(usuarioService).deleteById(1L);

        ResponseEntity<Void> response = usuarioController.deleteById(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}

package aceleramaker.project.controller;

import aceleramaker.project.dto.CreatePostagemDto;
import aceleramaker.project.dto.UpdatePostagemDto;
import aceleramaker.project.entity.Postagem;
import aceleramaker.project.entity.Tema;
import aceleramaker.project.entity.Usuario;
import aceleramaker.project.exceptions.ResourceNotFoundException;
import aceleramaker.project.service.PostagemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostagemControllerTest {

    private PostagemService postagemService;
    private PostagemController postagemController;

    @BeforeEach
    void setUp() {
        postagemService = mock(PostagemService.class);
        postagemController = new PostagemController(postagemService);
    }

    @Test
    void testCriar() {
        CreatePostagemDto dto = new CreatePostagemDto("Título", "Texto", 1L, 1L);
        Postagem postagem = new Postagem(
                1L,
                "Título",
                "Texto",
                new Tema(),
                new Usuario(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(postagemService.criar(dto)).thenReturn(postagem);

        ResponseEntity<Postagem> response = postagemController.criar(dto);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(postagem, response.getBody());
        verify(postagemService).criar(dto);
    }

    @Test
    void testListarTodas() {
        Page<Postagem> page = new PageImpl<>(List.of(new Postagem()));
        when(postagemService.listarTodas(any())).thenReturn(page);

        ResponseEntity<Page<Postagem>> response = postagemController.listarTodas(null, PageRequest.of(0, 10));

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(page, response.getBody());
        verify(postagemService).listarTodas(any());
    }

    @Test
    void testBuscarPorTitulo() {
        Page<Postagem> page = new PageImpl<>(List.of(new Postagem()));
        when(postagemService.buscarPorTitulo(eq("teste"), any())).thenReturn(page);

        ResponseEntity<Page<Postagem>> response = postagemController.listarTodas("teste", PageRequest.of(0, 10));

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(page, response.getBody());
        verify(postagemService).buscarPorTitulo(eq("teste"), any());
    }

    @Test
    void testBuscarPorId() {
        Postagem postagem = new Postagem(
                1L,
                "Título",
                "Texto",
                new Tema(),
                new Usuario(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        when(postagemService.buscarPorId(1L)).thenReturn(Optional.of(postagem));

        ResponseEntity<Postagem> response = postagemController.buscarPorId(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(postagem, response.getBody());
        verify(postagemService).buscarPorId(1L);
    }

    @Test
    void testAtualizar() {
        UpdatePostagemDto dto = new UpdatePostagemDto("Novo Título", "Novo Texto", 1L);
        Postagem postagemAtualizada = new Postagem(
                1L,
                "Novo Título",
                "Novo Texto",
                new Tema(),
                new Usuario(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(postagemService.atualizar(eq(1L), eq(dto))).thenReturn(Optional.of(postagemAtualizada));

        ResponseEntity<Postagem> response = postagemController.atualizar(1L, dto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(postagemAtualizada, response.getBody());
        verify(postagemService).atualizar(1L, dto);
    }

    @Test
    void testDeletar() {
        doNothing().when(postagemService).deletar(1L);

        ResponseEntity<Void> response = postagemController.deletar(1L);

        assertEquals(204, response.getStatusCodeValue());
        verify(postagemService).deletar(1L);
    }

    @Test
    void testBuscarPorTema() {
        Page<Postagem> page = new PageImpl<>(List.of(new Postagem()));
        when(postagemService.buscarPorTema(eq(1L), any())).thenReturn(page);

        ResponseEntity<Page<Postagem>> response = postagemController.buscarPorTema(1L, PageRequest.of(0, 10));

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(page, response.getBody());
        verify(postagemService).buscarPorTema(eq(1L), any());
    }

    @Test
    void testBuscarPorUsuario() {
        Page<Postagem> page = new PageImpl<>(List.of(new Postagem()));
        when(postagemService.buscarPorUsuario(eq(1L), any())).thenReturn(page);

        ResponseEntity<Page<Postagem>> response = postagemController.buscarPorUsuario(1L, PageRequest.of(0, 10));

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(page, response.getBody());
        verify(postagemService).buscarPorUsuario(eq(1L), any());
    }

    @Test
    void testBuscarPorId_NotFound() {
        when(postagemService.buscarPorId(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> postagemController.buscarPorId(99L)
        );

        assertEquals("Postagem não encontrada", exception.getMessage());
        verify(postagemService).buscarPorId(99L);
    }

    @Test
    void testAtualizar_NotFound() {
        UpdatePostagemDto dto = new UpdatePostagemDto("Novo Título", "Novo Texto", 1L);

        when(postagemService.atualizar(eq(99L), eq(dto))).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> postagemController.atualizar(99L, dto)
        );

        assertEquals("Postagem não encontrada", exception.getMessage());
        verify(postagemService).atualizar(99L, dto);
    }
}

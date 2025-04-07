package aceleramaker.project.controller;

import aceleramaker.project.dto.CreatePostagemDto;
import aceleramaker.project.dto.PostagemRespostaDto;
import aceleramaker.project.dto.UpdatePostagemDto;
import aceleramaker.project.dto.TemaRespostaDto;
import aceleramaker.project.dto.UsuarioRespostaDto;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Fulano");
        usuario.setUsername("fulano");
        usuario.setCreationTimestamp(Instant.now());

        Tema tema = new Tema();
        tema.setId(1L);
        tema.setDescricao("Tecnologia");

        Postagem postagem = new Postagem(
                1L,
                "Título",
                "Texto",
                tema,
                usuario,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        PostagemRespostaDto respostaDto = new PostagemRespostaDto(
                postagem.getId(),
                postagem.getTitulo(),
                postagem.getTexto(),
                new TemaRespostaDto(tema.getId(), tema.getDescricao()),
                new UsuarioRespostaDto(usuario.getNome(), usuario.getFoto(), usuario.getCreationTimestamp()),
                postagem.getCreationTimestamp(),
                postagem.getUpdateTimestamp()
        );

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("fulano");

        when(postagemService.criarDto(dto, "fulano")).thenReturn(respostaDto);

        ResponseEntity<PostagemRespostaDto> response = postagemController.criar(dto, userDetails);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        PostagemRespostaDto body = response.getBody();
        assertNotNull(body);
        assertEquals("Título", body.titulo());
        assertEquals("Texto", body.texto());
        assertEquals("Tecnologia", body.tema().descricao());
        assertEquals("Fulano", body.usuario().nome());

        verify(postagemService).criarDto(dto, "fulano");
    }

    @Test
    void testListarTodas() {
        PostagemRespostaDto dto = new PostagemRespostaDto(
                1L,
                "Título",
                "Texto",
                new TemaRespostaDto(1L, "Tecnologia"),
                new UsuarioRespostaDto("Fulano", null, Instant.now()),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        Page<PostagemRespostaDto> page = new PageImpl<>(List.of(dto));
        when(postagemService.listarTodas(any())).thenReturn(page);

        ResponseEntity<Page<PostagemRespostaDto>> response = postagemController.listarTodas(null, PageRequest.of(0, 10));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());
        assertEquals("Título", response.getBody().getContent().get(0).titulo());

        verify(postagemService).listarTodas(any());
    }

    @Test
    void testBuscarPorTitulo() {
        PostagemRespostaDto dto = new PostagemRespostaDto(
                1L,
                "Título",
                "Texto",
                new TemaRespostaDto(1L, "Tecnologia"),
                new UsuarioRespostaDto("Fulano", null, Instant.now()),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        Page<PostagemRespostaDto> page = new PageImpl<>(List.of(dto));
        when(postagemService.buscarPorTitulo("teste", PageRequest.of(0, 10))).thenReturn(page);

        ResponseEntity<Page<PostagemRespostaDto>> response = postagemController.listarTodas("teste", PageRequest.of(0, 10));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());
        assertEquals("Título", response.getBody().getContent().get(0).titulo());

        verify(postagemService).buscarPorTitulo("teste", PageRequest.of(0, 10));
    }

    @Test
    void testBuscarPorId() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Fulano");
        usuario.setUsername("fulano");
        usuario.setCreationTimestamp(Instant.now());

        Tema tema = new Tema();
        tema.setId(1L);
        tema.setDescricao("Tecnologia");

        Postagem postagem = new Postagem(
                1L,
                "Título",
                "Texto",
                tema,
                usuario,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        PostagemRespostaDto respostaDto = new PostagemRespostaDto(
                postagem.getId(),
                postagem.getTitulo(),
                postagem.getTexto(),
                new TemaRespostaDto(tema.getId(), tema.getDescricao()),
                new UsuarioRespostaDto(usuario.getNome(), usuario.getFoto(), usuario.getCreationTimestamp()),
                postagem.getCreationTimestamp(),
                postagem.getUpdateTimestamp()
        );

        when(postagemService.buscarPorIdDto(1L)).thenReturn(Optional.of(respostaDto));

        ResponseEntity<PostagemRespostaDto> response = postagemController.buscarPorId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(respostaDto, response.getBody());
        verify(postagemService).buscarPorIdDto(1L);
    }

    @Test
    void testAtualizar() {
        UpdatePostagemDto dto = new UpdatePostagemDto("Novo Título", "Novo Texto", 1L);
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Fulano");
        usuario.setUsername("fulano");
        usuario.setCreationTimestamp(Instant.now());

        Tema tema = new Tema();
        tema.setId(1L);
        tema.setDescricao("Tecnologia");

        Postagem postagemAtualizada = new Postagem(
                1L,
                "Novo Título",
                "Novo Texto",
                tema,
                usuario,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        PostagemRespostaDto respostaDto = new PostagemRespostaDto(
                postagemAtualizada.getId(),
                postagemAtualizada.getTitulo(),
                postagemAtualizada.getTexto(),
                new TemaRespostaDto(tema.getId(), tema.getDescricao()),
                new UsuarioRespostaDto(usuario.getNome(), usuario.getFoto(), usuario.getCreationTimestamp()),
                postagemAtualizada.getCreationTimestamp(),
                postagemAtualizada.getUpdateTimestamp()
        );

        when(postagemService.atualizarDto(1L, dto)).thenReturn(Optional.of(respostaDto));

        ResponseEntity<PostagemRespostaDto> response = postagemController.atualizar(1L, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(respostaDto, response.getBody());
        verify(postagemService).atualizarDto(1L, dto);
    }

    @Test
    void testDeletar() {
        doNothing().when(postagemService).deletar(1L);

        ResponseEntity<Map<String, String>> response = postagemController.deletar(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Postagem excluída com sucesso.", response.getBody().get("mensagem"));
        verify(postagemService).deletar(1L);
    }

    @Test
    void testBuscarPorTema() {
        PostagemRespostaDto dto = new PostagemRespostaDto(
                1L,
                "Título",
                "Texto",
                new TemaRespostaDto(1L, "Tecnologia"),
                new UsuarioRespostaDto("Fulano", null, Instant.now()),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        Page<PostagemRespostaDto> page = new PageImpl<>(List.of(dto));
        when(postagemService.buscarPorTema(1L, PageRequest.of(0, 10))).thenReturn(page);

        ResponseEntity<Page<PostagemRespostaDto>> response = postagemController.buscarPorTema(1L, PageRequest.of(0, 10));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(page, response.getBody());
        verify(postagemService).buscarPorTema(1L, PageRequest.of(0, 10));
    }

    @Test
    void testBuscarPorUsuario() {
        PostagemRespostaDto dto = new PostagemRespostaDto(
                1L,
                "Título",
                "Texto",
                new TemaRespostaDto(1L, "Tecnologia"),
                new UsuarioRespostaDto("Fulano", null, Instant.now()),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        Page<PostagemRespostaDto> page = new PageImpl<>(List.of(dto));
        when(postagemService.buscarPorUsuario(1L, PageRequest.of(0, 10))).thenReturn(page);

        ResponseEntity<Page<PostagemRespostaDto>> response = postagemController.buscarPorUsuario(1L, PageRequest.of(0, 10));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(page, response.getBody());
        verify(postagemService).buscarPorUsuario(1L, PageRequest.of(0, 10));
    }

    @Test
    void testBuscarPorId_NotFound() {
        when(postagemService.buscarPorIdDto(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> postagemController.buscarPorId(99L));

        verify(postagemService).buscarPorIdDto(99L);
    }

    @Test
    void testAtualizar_NotFound() {
        UpdatePostagemDto dto = new UpdatePostagemDto("Novo Título", "Novo Texto", 1L);

        when(postagemService.atualizarDto(99L, dto)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> postagemController.atualizar(99L, dto));

        verify(postagemService).atualizarDto(99L, dto);
    }
}
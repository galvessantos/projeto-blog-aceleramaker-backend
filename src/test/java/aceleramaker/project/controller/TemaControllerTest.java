package aceleramaker.project.controller;

import aceleramaker.project.dto.CreateTemaDto;
import aceleramaker.project.entity.Tema;
import aceleramaker.project.exceptions.ResourceNotFoundException;
import aceleramaker.project.service.TemaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TemaControllerTest {

    private TemaService temaService;
    private TemaController temaController;

    @BeforeEach
    void setUp() {
        temaService = Mockito.mock(TemaService.class);
        temaController = new TemaController(temaService);
    }

    @Test
    void testCriarTema() {
        CreateTemaDto dto = new CreateTemaDto("Tecnologia");
        Tema temaCriado = new Tema(1L, "Tecnologia", null);

        when(temaService.criar(dto)).thenReturn(temaCriado);

        ResponseEntity<Tema> response = temaController.criar(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(temaCriado, response.getBody());
    }

    @Test
    void testListarTodosSemFiltro() {
        List<Tema> temas = List.of(new Tema(), new Tema());
        when(temaService.listarTodos()).thenReturn(temas);

        ResponseEntity<List<Tema>> response = temaController.listarTodos(null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(temas, response.getBody());
    }

    @Test
    void testListarTodosComFiltro() {
        String descricao = "tech";
        List<Tema> temas = List.of(new Tema());
        when(temaService.buscarPorDescricaoParcial(descricao)).thenReturn(temas);

        ResponseEntity<List<Tema>> response = temaController.listarTodos(descricao);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(temas, response.getBody());
    }

    @Test
    void testBuscarPorId_Encontrado() {
        Tema tema = new Tema(1L, "Saúde", null);
        when(temaService.buscarPorId(1L)).thenReturn(Optional.of(tema));

        ResponseEntity<Tema> response = temaController.buscarPorId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(tema, response.getBody());
    }

    @Test
    void testBuscarPorId_NaoEncontrado() {
        when(temaService.buscarPorId(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> temaController.buscarPorId(1L));
    }

    @Test
    void testAtualizar_Encontrado() {
        CreateTemaDto dto = new CreateTemaDto("Atualizado");
        Tema atualizado = new Tema(1L, "Atualizado", null);
        when(temaService.atualizar(1L, dto)).thenReturn(Optional.of(atualizado));

        ResponseEntity<Tema> response = temaController.atualizar(1L, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(atualizado, response.getBody());
    }

    @Test
    void testAtualizar_NaoEncontrado() {
        CreateTemaDto dto = new CreateTemaDto("Atualizado");
        when(temaService.atualizar(1L, dto)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> temaController.atualizar(1L, dto));
    }

    @Test
    void testDeletar() {
        doNothing().when(temaService).deletar(1L);

        ResponseEntity<Void> response = temaController.deletar(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}

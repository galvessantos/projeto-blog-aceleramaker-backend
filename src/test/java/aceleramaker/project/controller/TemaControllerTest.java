package aceleramaker.project.controller;

import aceleramaker.project.dto.CreateTemaDto;
import aceleramaker.project.dto.TemaRespostaDto;
import aceleramaker.project.entity.Tema;
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

        ResponseEntity<TemaRespostaDto> response = temaController.criar(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().id());
        assertEquals("Tecnologia", response.getBody().descricao());
    }

    @Test
    void testListarTodosSemFiltro() {
        List<Tema> temas = List.of(
                new Tema(1L, "Tecnologia", null),
                new Tema(2L, "Saúde", null)
        );
        when(temaService.listarTodos()).thenReturn(temas);

        ResponseEntity<List<TemaRespostaDto>> response = temaController.listarTodos(null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertEquals("Tecnologia", response.getBody().get(0).descricao());
        assertEquals("Saúde", response.getBody().get(1).descricao());
    }

    @Test
    void testListarTodosComFiltro() {
        String descricao = "tech";
        List<Tema> temas = List.of(new Tema(1L, "Tecnologia", null));
        when(temaService.buscarPorDescricaoParcial(descricao)).thenReturn(temas);

        ResponseEntity<List<TemaRespostaDto>> response = temaController.listarTodos(descricao);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Tecnologia", response.getBody().get(0).descricao());
    }

    @Test
    void testAtualizar_Encontrado() {
        CreateTemaDto dto = new CreateTemaDto("Atualizado");
        Tema atualizado = new Tema(1L, "Atualizado", null);
        when(temaService.atualizar(1L, dto)).thenReturn(Optional.of(atualizado));

        ResponseEntity<TemaRespostaDto> response = temaController.atualizar(1L, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().id());
        assertEquals("Atualizado", response.getBody().descricao());
    }
}

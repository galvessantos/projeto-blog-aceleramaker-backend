package aceleramaker.project.service;

import aceleramaker.project.dto.CreateTemaDto;
import aceleramaker.project.entity.Tema;
import aceleramaker.project.exceptions.ResourceNotFoundException;
import aceleramaker.project.repository.TemaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class TemaServiceTest {

    @Mock
    private TemaRepository temaRepository;

    @InjectMocks
    private TemaService temaService;

    private Tema tema;

    @BeforeEach
    void setup() {
        tema = new Tema();
        tema.setId(1L);
        tema.setDescricao("Tecnologia");
    }

    @Test
    void deveCriarTemaComSucesso() {
        CreateTemaDto dto = new CreateTemaDto("Tecnologia");

        when(temaRepository.save(any(Tema.class))).thenReturn(tema);

        Tema resultado = temaService.criar(dto);

        assertNotNull(resultado);
        assertEquals("Tecnologia", resultado.getDescricao());
        verify(temaRepository, times(1)).save(any(Tema.class));
    }

    @Test
    void deveListarTodosTemas() {
        when(temaRepository.findAll()).thenReturn(List.of(tema));

        List<Tema> temas = temaService.listarTodos();

        assertEquals(1, temas.size());
        verify(temaRepository).findAll();
    }

    @Test
    void deveAtualizarTemaComSucesso() {
        CreateTemaDto dto = new CreateTemaDto("Atualizado");

        when(temaRepository.findById(1L)).thenReturn(Optional.of(tema));
        when(temaRepository.save(any(Tema.class))).thenReturn(tema);

        Optional<Tema> atualizado = temaService.atualizar(1L, dto);

        assertTrue(atualizado.isPresent());
        assertEquals("Atualizado", atualizado.get().getDescricao());
        verify(temaRepository).save(tema);
    }

    @Test
    void deveLancarExcecao_AtualizarTemaInexistente() {
        CreateTemaDto dto = new CreateTemaDto("Novo");

        when(temaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> temaService.atualizar(99L, dto));
    }

    @Test
    void deveDeletarTemaComSucesso() {
        when(temaRepository.existsById(1L)).thenReturn(true);

        temaService.deletar(1L);

        verify(temaRepository).deleteById(1L);
    }

    @Test
    void deveLancarExcecao_DeletarTemaInexistente() {
        when(temaRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> temaService.deletar(99L));
    }

    @Test
    void deveBuscarTemaPorId() {
        when(temaRepository.findById(1L)).thenReturn(Optional.of(tema));

        Optional<Tema> encontrado = temaService.buscarPorId(1L);

        assertTrue(encontrado.isPresent());
        assertEquals("Tecnologia", encontrado.get().getDescricao());
    }

    @Test
    void deveBuscarTemasPorDescricaoParcial() {
        when(temaRepository.findByDescricaoContainingIgnoreCase("tec")).thenReturn(List.of(tema));

        List<Tema> resultados = temaService.buscarPorDescricaoParcial("tec");

        assertEquals(1, resultados.size());
        assertEquals("Tecnologia", resultados.get(0).getDescricao());
    }
}

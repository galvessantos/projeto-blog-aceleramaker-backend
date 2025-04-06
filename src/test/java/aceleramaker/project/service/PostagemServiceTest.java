package aceleramaker.project.service;

import aceleramaker.project.dto.CreatePostagemDto;
import aceleramaker.project.dto.UpdatePostagemDto;
import aceleramaker.project.entity.Postagem;
import aceleramaker.project.entity.Tema;
import aceleramaker.project.entity.Usuario;
import aceleramaker.project.exceptions.ResourceNotFoundException;
import aceleramaker.project.repository.PostagemRepository;
import aceleramaker.project.repository.TemaRepository;
import aceleramaker.project.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class PostagemServiceTest {

    @Mock private PostagemRepository postagemRepository;
    @Mock private TemaRepository temaRepository;
    @Mock private UsuarioRepository usuarioRepository;

    @InjectMocks private PostagemService postagemService;

    private Tema tema;
    private Usuario usuario;
    private Postagem postagem;

    @BeforeEach
    void setUp() {
        tema = new Tema(1L, "Programação", new ArrayList<>());
        usuario = new Usuario(1L, "João", "joao", "joao@email.com", "senha", "foto", new ArrayList<>(), null, null, null);
        postagem = new Postagem();
        postagem.setId(1L);
        postagem.setTitulo("Título");
        postagem.setTexto("Texto");
        postagem.setTema(tema);
        postagem.setUsuario(usuario);
    }

    @Test
    void deveListarTodasAsPostagens() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Postagem> page = new PageImpl<>(List.of(postagem));
        when(postagemRepository.findAll(pageable)).thenReturn(page);

        var resultado = postagemService.listarTodas(pageable);

        assertEquals(1, resultado.getTotalElements());
    }

    @Test
    void deveBuscarPorId() {
        when(postagemRepository.findById(1L)).thenReturn(Optional.of(postagem));

        var resultado = postagemService.buscarPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Título", resultado.get().getTitulo());
    }

    @Test
    void deveBuscarPorTitulo() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Postagem> page = new PageImpl<>(List.of(postagem));
        when(postagemRepository.findByTituloContainingIgnoreCase("título", pageable)).thenReturn(page);

        var resultado = postagemService.buscarPorTitulo("título", pageable);

        assertEquals(1, resultado.getTotalElements());
    }

    @Test
    void deveCriarPostagem() {
        CreatePostagemDto dto = new CreatePostagemDto("Novo título", "Novo texto", 1L, 1L);

        when(temaRepository.findById(1L)).thenReturn(Optional.of(tema));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(postagemRepository.save(any(Postagem.class))).thenReturn(postagem);

        Postagem criada = postagemService.criar(dto);

        assertEquals("Título", criada.getTitulo());
        verify(postagemRepository).save(any(Postagem.class));
    }

    @Test
    void deveLancarExcecaoAoCriarComTemaInexistente() {
        CreatePostagemDto dto = new CreatePostagemDto("Titulo", "Texto", 99L, 1L);
        when(temaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> postagemService.criar(dto));
    }

    @Test
    void deveLancarExcecaoAoCriarComUsuarioInexistente() {
        CreatePostagemDto dto = new CreatePostagemDto("Titulo", "Texto", 1L, 99L);
        when(temaRepository.findById(1L)).thenReturn(Optional.of(tema));
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> postagemService.criar(dto));
    }

    @Test
    void deveAtualizarPostagem() {
        UpdatePostagemDto dto = new UpdatePostagemDto("Atualizado", "Texto atualizado", 1L);

        when(postagemRepository.findById(1L)).thenReturn(Optional.of(postagem));
        when(temaRepository.findById(1L)).thenReturn(Optional.of(tema));
        when(postagemRepository.save(any(Postagem.class))).thenReturn(postagem);

        Optional<Postagem> atualizada = postagemService.atualizar(1L, dto);

        assertTrue(atualizada.isPresent());
        assertEquals("Atualizado", atualizada.get().getTitulo());
    }

    @Test
    void deveLancarExcecaoAoAtualizarComTemaInexistente() {
        UpdatePostagemDto dto = new UpdatePostagemDto("Atualizado", "Texto", 99L);
        when(postagemRepository.findById(1L)).thenReturn(Optional.of(postagem));
        when(temaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> postagemService.atualizar(1L, dto));
    }

    @Test
    void deveDeletarPostagemComSucesso() {
        when(postagemRepository.existsById(1L)).thenReturn(true);

        postagemService.deletar(1L);

        verify(postagemRepository).deleteById(1L);
    }

    @Test
    void deveLancarExcecaoAoDeletarPostagemInexistente() {
        when(postagemRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> postagemService.deletar(99L));
    }

    @Test
    void deveBuscarPorTema() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Postagem> page = new PageImpl<>(List.of(postagem));
        when(postagemRepository.findByTemaId(1L, pageable)).thenReturn(page);

        var resultado = postagemService.buscarPorTema(1L, pageable);

        assertEquals(1, resultado.getTotalElements());
    }

    @Test
    void deveBuscarPorUsuario() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Postagem> page = new PageImpl<>(List.of(postagem));
        when(postagemRepository.findByUsuarioId(1L, pageable)).thenReturn(page);

        var resultado = postagemService.buscarPorUsuario(1L, pageable);

        assertEquals(1, resultado.getTotalElements());
    }
}

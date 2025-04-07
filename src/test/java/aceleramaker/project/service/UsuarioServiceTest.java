package aceleramaker.project.service;

import aceleramaker.project.dto.CreateUsuarioDto;
import aceleramaker.project.dto.UpdateUsuarioDto;
import aceleramaker.project.entity.Usuario;
import aceleramaker.project.enums.Role;
import aceleramaker.project.exceptions.ResourceNotFoundException;
import aceleramaker.project.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setup() {
        usuario = new Usuario(
                1L, "João", "joaozinho", "joao@email.com",
                "senhaCriptografada", null,
                new ArrayList<>(), Instant.now(), null, Role.USER
        );

        var auth = new UsernamePasswordAuthenticationToken("joaozinho", null, new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void deveCriarUsuarioComSucesso() {
        CreateUsuarioDto dto = new CreateUsuarioDto("João", "joaozinho", "joao@email.com", "123456");

        when(passwordEncoder.encode("123456")).thenReturn("senhaCriptografada");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Long id = usuarioService.createUsuario(dto);

        assertEquals(1L, id);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void deveRetornarUsuarioPorId() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Optional<Usuario> resultado = usuarioService.getUsuarioById(1L);

        assertTrue(resultado.isPresent());
        assertEquals("João", resultado.get().getNome());
    }

    @Test
    void deveListarUsuarios() {
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));

        List<Usuario> lista = usuarioService.listUsers();

        assertEquals(1, lista.size());
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    void deveAtualizarUsuarioComSucesso() {
        UpdateUsuarioDto dto = new UpdateUsuarioDto("novoNome", "novoUsername", "novaSenha", null);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("novaSenha")).thenReturn("senhaNovaCriptografada");

        usuarioService.updateUsuarioById(1L, dto);

        assertEquals("novoNome", usuario.getNome());
        assertEquals("novoUsername", usuario.getUsername());
        assertEquals("senhaNovaCriptografada", usuario.getSenha());

        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    void deveLancarExcecao_AtualizarUsuarioNaoExistente() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        UpdateUsuarioDto dto = new UpdateUsuarioDto("nome", "username", "senha", null);

        assertThrows(ResourceNotFoundException.class, () -> {
            usuarioService.updateUsuarioById(99L, dto);
        });
    }

    @Test
    void deveLancarAccessDeniedException_AtualizarOutroUsuario() {
        usuario.setUsername("outroUsuario");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        UpdateUsuarioDto dto = new UpdateUsuarioDto("nome", "username", "senha", null);

        assertThrows(AccessDeniedException.class, () -> {
            usuarioService.updateUsuarioById(1L, dto);
        });
    }

    @Test
    void deveDeletarUsuarioComSucesso() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        usuarioService.deleteById(1L);

        verify(usuarioRepository, times(1)).deleteById(1L);
    }

    @Test
    void deveLancarExcecao_UsuarioNaoExisteParaDelecao() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            usuarioService.deleteById(99L);
        });
    }

    @Test
    void deveLancarAccessDeniedException_DeletarOutroUsuario() {
        usuario.setUsername("outroUsuario");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        assertThrows(AccessDeniedException.class, () -> {
            usuarioService.deleteById(1L);
        });
    }
}

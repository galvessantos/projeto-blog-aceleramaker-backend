package aceleramaker.project.service;

import aceleramaker.project.entity.Usuario;
import aceleramaker.project.enums.Role;
import aceleramaker.project.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MyUserDetailsServiceTest {

    private UsuarioRepository usuarioRepository;
    private MyUserDetailsService myUserDetailsService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuarioRepository = mock(UsuarioRepository.class);
        myUserDetailsService = new MyUserDetailsService(usuarioRepository);

        usuario = new Usuario(
                1L, "João", "joaozinho", "joao@email.com",
                "senhaCriptografada", "foto.png",
                new ArrayList<>(), Instant.now(), null, Role.USER
        );
    }

    @Test
    void deveCarregarUsuarioPorUsernameOuEmail() {
        when(usuarioRepository.findByUsernameOrEmail("joao", "joao"))
                .thenReturn(Optional.of(usuario));

        UserDetails userDetails = myUserDetailsService.loadUserByUsername("joao");

        assertNotNull(userDetails);
        assertEquals("joaozinho", userDetails.getUsername());
    }

    @Test
    void deveLancarExcecao_UsuarioNaoEncontrado() {
        when(usuarioRepository.findByUsernameOrEmail("inexistente", "inexistente"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            myUserDetailsService.loadUserByUsername("inexistente");
        });
    }
}

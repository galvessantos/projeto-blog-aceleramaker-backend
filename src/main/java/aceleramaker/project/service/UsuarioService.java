package aceleramaker.project.service;

import aceleramaker.project.dto.CreateUsuarioDto;
import aceleramaker.project.dto.UpdateUsuarioDto;
import aceleramaker.project.entity.Usuario;
import aceleramaker.project.enums.Role;
import aceleramaker.project.exceptions.ResourceNotFoundException;
import aceleramaker.project.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService implements UserDetailsService {

    private final BCryptPasswordEncoder encoder;
    private final UsuarioRepository usuarioRepository;

    public UsuarioService(BCryptPasswordEncoder encoder, UsuarioRepository usuarioRepository) {
        this.encoder = encoder;
        this.usuarioRepository = usuarioRepository;
    }

    public Long createUsuario(CreateUsuarioDto createUsuarioDto) {
        var entity = new Usuario(null,
                createUsuarioDto.nome(),
                createUsuarioDto.username(),
                createUsuarioDto.email(),
                encoder.encode(createUsuarioDto.senha()),
                null,
                Collections.emptyList(),
                Instant.now(),
                null,
                Role.USER);

        var usuarioSalvo = usuarioRepository.save(entity);
        return usuarioSalvo.getId();
    }

    public Optional<Usuario> getUsuarioById(String usuarioId) {
        return usuarioRepository.findById(Long.valueOf(usuarioId));
    }

    public List<Usuario> listUsers() {
        return usuarioRepository.findAll();
    }

    public void updateUsuarioById(String usuarioId, UpdateUsuarioDto updateUsuarioDto) {
        var id = Long.valueOf(usuarioId);

        var usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + usuarioId));

        if (updateUsuarioDto.username() != null) {
            usuario.setUsername(updateUsuarioDto.username());
        }

        if (updateUsuarioDto.foto() != null) {
            usuario.setFoto(updateUsuarioDto.foto());
        }

        if (updateUsuarioDto.nome() != null) {
            usuario.setNome(updateUsuarioDto.nome());
        }

        if (updateUsuarioDto.senha() != null) {
            usuario.setSenha(encoder.encode(updateUsuarioDto.senha()));
        }

        usuarioRepository.save(usuario);
    }

    public void deleteById(String usuarioId) {
        var id = Long.valueOf(usuarioId);

        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuário não encontrado com ID: " + usuarioId);
        }

        usuarioRepository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        return usuarioRepository.findByUsernameOrEmail(login, login)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário ou Email não encontrado: " + login));
    }
}

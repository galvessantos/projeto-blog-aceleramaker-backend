package aceleramaker.project.service;

import aceleramaker.project.dto.CreateUsuarioDto;
import aceleramaker.project.entity.Usuario;
import aceleramaker.project.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;

@Service
public class UsuarioService {

    private UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Long createUsuario (CreateUsuarioDto createUsuarioDto) {
        var entity = new Usuario(null,
                createUsuarioDto.nome(),
                createUsuarioDto.usuario(),
                createUsuarioDto.email(),
                createUsuarioDto.senha(),
                null,
                Collections.emptyList(),
                Instant.now(),
                null);

        var usuarioSalvo = usuarioRepository.save(entity);

        return usuarioSalvo.getId();
    }
}

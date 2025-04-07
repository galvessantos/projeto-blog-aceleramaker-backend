package aceleramaker.project.dto;

import java.time.LocalDateTime;

public record PostagemRespostaDto(
        Long id,
        String titulo,
        String texto,
        TemaRespostaDto tema,
        UsuarioRespostaDto usuario,
        LocalDateTime creationTimestamp,
        LocalDateTime updateTimestamp
) {}


package aceleramaker.project.dto;

public record UsuarioRespostaDto(
        Long id,
        String nome,
        String username,
        String email,
        String foto,
        java.time.Instant creationTimestamp
) {}
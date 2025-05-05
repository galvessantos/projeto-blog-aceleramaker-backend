package aceleramaker.project.dto;

public record UsuarioRespostaDto(
        String nome,
        String username,
        String email,
        String foto,
        java.time.Instant creationTimestamp
) {}
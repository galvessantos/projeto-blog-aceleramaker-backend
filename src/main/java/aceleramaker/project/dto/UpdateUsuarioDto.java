package aceleramaker.project.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateUsuarioDto(
        @NotBlank(message = "O nome não pode estar em branco") String nome,
        String senha,
        String foto
) {}

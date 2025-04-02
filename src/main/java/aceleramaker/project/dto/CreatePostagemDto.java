package aceleramaker.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreatePostagemDto(
        @NotBlank String titulo,
        @NotBlank String texto,
        @NotNull Long temaId,
        @NotNull Long usuarioId
) {}

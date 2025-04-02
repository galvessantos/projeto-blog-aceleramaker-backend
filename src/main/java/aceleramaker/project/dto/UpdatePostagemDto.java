package aceleramaker.project.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdatePostagemDto(
        @NotBlank String titulo,
        @NotBlank String texto,
        Long temaId
) {}

package aceleramaker.project.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdatePostagemDto(
        @NotBlank(message = "O título não pode estar em branco") String titulo,
        @NotBlank(message = "O texto não pode estar em branco") String texto,
        Long temaId
) {}

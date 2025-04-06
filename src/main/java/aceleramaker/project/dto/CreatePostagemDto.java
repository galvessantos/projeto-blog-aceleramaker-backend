package aceleramaker.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreatePostagemDto(
        @NotBlank(message = "O título não pode estar em branco")
        @Size(max = 100, message = "O título deve ter no máximo 100 caracteres")
        String titulo,

        @NotBlank(message = "O texto não pode estar em branco")
        @Size(max = 1000, message = "O texto deve ter no máximo 1000 caracteres")
        String texto,

        @NotNull(message = "O temaId é obrigatório")
        Long temaId,

        @NotNull(message = "O usuarioId é obrigatório")
        Long usuarioId
) {}

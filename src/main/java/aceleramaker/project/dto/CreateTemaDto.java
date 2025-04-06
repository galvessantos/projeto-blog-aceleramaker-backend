package aceleramaker.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTemaDto(
        @NotBlank(message = "A descrição do tema não pode estar em branco")
        @Size(max = 255, message = "A descrição deve ter no máximo 255 caracteres")
        String descricao
) {}

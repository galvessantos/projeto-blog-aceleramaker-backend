package aceleramaker.project.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateTemaDto(
        @NotBlank(message = "A descrição do tema não pode estar vazia") String descricao
) {}

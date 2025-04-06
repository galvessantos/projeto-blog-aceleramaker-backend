package aceleramaker.project.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateUsuarioDto(
        @NotBlank(message = "O nome não pode estar em branco") String nome,
        @NotBlank(message = "O username não pode estar em branco") String username,
        @NotBlank(message = "A senha não pode estar em branco") String senha,
        String foto
) {}

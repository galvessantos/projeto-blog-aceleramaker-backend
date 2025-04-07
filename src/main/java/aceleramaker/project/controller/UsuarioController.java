package aceleramaker.project.controller;

import aceleramaker.project.dto.UpdateUsuarioDto;
import aceleramaker.project.dto.UsuarioRespostaDto;
import aceleramaker.project.entity.Usuario;
import aceleramaker.project.exceptions.AccessDeniedCustomException;
import aceleramaker.project.exceptions.ResourceNotFoundException;
import aceleramaker.project.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/usuarios")
@Tag(name = "02 - Usuários", description = "Operações relacionadas à conta do usuário")
public class UsuarioController {

    private static final String USUARIO_NAO_ENCONTRADO = "Usuário não encontrado com ID: ";

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/{usuarioId}")
    @Operation(summary = "Buscar usuário por ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso."),
                    @ApiResponse(responseCode = "404", description = "Usuário não encontrado.")
            }
    )
    public ResponseEntity<UsuarioRespostaDto> getUsuarioById(@PathVariable Long usuarioId) {
        Usuario usuario = usuarioService.getUsuarioById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException(USUARIO_NAO_ENCONTRADO + usuarioId));

        UsuarioRespostaDto resposta = new UsuarioRespostaDto(
                usuario.getNome(),
                usuario.getFoto(),
                usuario.getCreationTimestamp()
        );

        return ResponseEntity.ok(resposta);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar todos os usuários (apenas para administradores)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso."),
                    @ApiResponse(responseCode = "403", description = "Acesso negado.")
            }
    )
    public ResponseEntity<List<UsuarioRespostaDto>> listUsuarios() {
        List<Usuario> usuarios = usuarioService.listUsers();

        List<UsuarioRespostaDto> resposta = usuarios.stream()
                .map(usuario -> new UsuarioRespostaDto(
                        usuario.getNome(),
                        usuario.getFoto(),
                        usuario.getCreationTimestamp()
                ))
                .toList();

        return ResponseEntity.ok(resposta);
    }

    @PutMapping("/{usuarioId}")
    @Operation(summary = "Atualizar dados de um usuário (autorizado apenas para o próprio usuário)",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Usuário atualizado com sucesso."),
                    @ApiResponse(responseCode = "403", description = "Acesso negado."),
                    @ApiResponse(responseCode = "404", description = "Usuário não encontrado.")
            }
    )
    public ResponseEntity<Void> updateUsuarioById(
            @Parameter(description = "ID do usuário") @PathVariable Long usuarioId,
            @RequestBody @Valid UpdateUsuarioDto updateUsuarioDto,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails current) {

        Usuario alvo = usuarioService.getUsuarioById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException(USUARIO_NAO_ENCONTRADO + usuarioId));

        if (!current.getUsername().equals(alvo.getUsername())) {
            throw new AccessDeniedCustomException("Você só pode atualizar sua própria conta.");
        }

        usuarioService.updateUsuarioById(usuarioId, updateUsuarioDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{usuarioId}")
    @Operation(summary = "Deletar o próprio usuário",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Usuário deletado com sucesso."),
                    @ApiResponse(responseCode = "403", description = "Acesso negado."),
                    @ApiResponse(responseCode = "404", description = "Usuário não encontrado.")
            }
    )
    public ResponseEntity<Void> deleteById(
            @Parameter(description = "ID do usuário") @PathVariable Long usuarioId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails current) {

        Usuario alvo = usuarioService.getUsuarioById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException(USUARIO_NAO_ENCONTRADO + usuarioId));

        if (!current.getUsername().equals(alvo.getUsername())) {
            throw new AccessDeniedException("Você só pode excluir sua própria conta.");
        }

        usuarioService.deleteById(usuarioId);
        return ResponseEntity.noContent().build();
    }
}

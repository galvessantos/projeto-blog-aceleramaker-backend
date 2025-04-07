package aceleramaker.project.controller;

import aceleramaker.project.dto.CreateUsuarioDto;
import aceleramaker.project.dto.UpdateUsuarioDto;
import aceleramaker.project.entity.Usuario;
import aceleramaker.project.exceptions.ResourceNotFoundException;
import aceleramaker.project.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/v1/usuarios")
@Tag(name = "Usuários", description = "Operações relacionadas aos usuários do sistema")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    @Operation(summary = "Cadastrar um novo usuário")
    public ResponseEntity<Void> createUser(
            @RequestBody @Valid CreateUsuarioDto createUsuarioDto) {
        Long usuarioId = usuarioService.createUsuario(createUsuarioDto);
        return ResponseEntity.created(URI.create("/v1/usuarios/" + usuarioId)).build();
    }

    @GetMapping("/{usuarioId}")
    @Operation(summary = "Buscar usuário por ID")
    public ResponseEntity<Usuario> getUsuarioById(
            @Parameter(description = "ID do usuário") @PathVariable Long usuarioId) {
        Usuario usuario = usuarioService.getUsuarioById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + usuarioId));
        return ResponseEntity.ok(usuario);
    }

    @GetMapping
    @Operation(summary = "Listar todos os usuários")
    public ResponseEntity<List<Usuario>> listUsuarios() {
        List<Usuario> usuarios = usuarioService.listUsers();
        return ResponseEntity.ok(usuarios);
    }

    @PutMapping("/{usuarioId}")
    @Operation(summary = "Atualizar dados de um usuário")
    public ResponseEntity<Void> updateUsuarioById(
            @Parameter(description = "ID do usuário") @PathVariable Long usuarioId,
            @RequestBody @Valid UpdateUsuarioDto updateUsuarioDto) {
        usuarioService.updateUsuarioById(usuarioId, updateUsuarioDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{usuarioId}")
    @Operation(summary = "Deletar o próprio usuário")
    public ResponseEntity<Void> deleteById(
            @Parameter(description = "ID do usuário") @PathVariable Long usuarioId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails current) {

        Usuario alvo = usuarioService.getUsuarioById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + usuarioId));

        if (!current.getUsername().equals(alvo.getUsername())) {
            throw new AccessDeniedException("Você só pode excluir sua própria conta.");
        }

        usuarioService.deleteById(usuarioId);
        return ResponseEntity.noContent().build();
    }
}

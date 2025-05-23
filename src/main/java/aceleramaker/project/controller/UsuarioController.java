package aceleramaker.project.controller;

import aceleramaker.project.dto.AlterarSenhaRequestDto;
import aceleramaker.project.dto.UpdateUsuarioDto;
import aceleramaker.project.dto.UsuarioRespostaDto;
import aceleramaker.project.entity.Usuario;
import aceleramaker.project.exceptions.AccessDeniedCustomException;
import aceleramaker.project.exceptions.ResourceNotFoundException;
import aceleramaker.project.repository.UsuarioRepository;
import aceleramaker.project.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.util.Map;
import java.util.HashMap;

import java.util.List;

@RestController
@RequestMapping("/v1/usuarios")
@Tag(name = "02 - Usuários", description = "Operações relacionadas à conta do usuário")
@CrossOrigin(origins = "http://localhost:4200")
public class UsuarioController {

    private static final String USUARIO_NAO_ENCONTRADO = "Usuário não encontrado com ID: ";

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioController(UsuarioService usuarioService, UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
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
                usuario.getUsername(),
                usuario.getEmail(),
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
                        usuario.getUsername(),
                        usuario.getEmail(),
                        usuario.getFoto(),
                        usuario.getCreationTimestamp()
                ))
                .toList();

        return ResponseEntity.ok(resposta);
    }

    @PutMapping("/perfil")
    @Operation(summary = "Atualizar perfil do usuário logado com foto",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Perfil atualizado com sucesso."),
                    @ApiResponse(responseCode = "400", description = "Requisição inválida."),
                    @ApiResponse(responseCode = "401", description = "Não autenticado.")
            }
    )
    public ResponseEntity<UsuarioRespostaDto> atualizarPerfil(
            @RequestPart("nome") String nome,
            @RequestPart(value = "foto", required = false) MultipartFile foto,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {

        Usuario usuario = usuarioRepository.findByUsernameOrEmail(userDetails.getUsername(), userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        String fotoUrl = null;
        if (foto != null && !foto.isEmpty()) {
            fotoUrl = usuarioService.salvarFoto(foto);
        }

        UpdateUsuarioDto updateDto = new UpdateUsuarioDto(
                nome,
                null,
                fotoUrl != null ? fotoUrl : usuario.getFoto()
        );

        usuarioService.updateUsuarioById(usuario.getId(), updateDto);


        Usuario usuarioAtualizado = usuarioService.getUsuarioById(usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        UsuarioRespostaDto resposta = new UsuarioRespostaDto(
                usuarioAtualizado.getNome(),
                usuarioAtualizado.getUsername(),
                usuarioAtualizado.getEmail(),
                usuarioAtualizado.getFoto(),
                usuarioAtualizado.getCreationTimestamp()
        );

        return ResponseEntity.ok(resposta);
    }

    @PostMapping("/alterar-senha")
    @Operation(summary = "Alterar senha do usuário logado",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Senha alterada com sucesso."),
                    @ApiResponse(responseCode = "400", description = "Senha atual inválida."),
                    @ApiResponse(responseCode = "401", description = "Não autenticado.")
            }
    )
    public ResponseEntity<Void> alterarSenha(
            @RequestBody @Valid AlterarSenhaRequestDto request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {

        Usuario usuario = usuarioRepository.findByUsernameOrEmail(userDetails.getUsername(), userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (!passwordEncoder.matches(request.senhaAtual(), usuario.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Senha atual inválida");
        }

        UpdateUsuarioDto updateDto = new UpdateUsuarioDto(
                usuario.getNome(),
                request.novaSenha(),
                usuario.getFoto()
        );

        usuarioService.updateUsuarioById(usuario.getId(), updateDto);
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

    @GetMapping("/me")
    @Operation(summary = "Buscar dados do usuário logado",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso."),
                    @ApiResponse(responseCode = "401", description = "Não autenticado.")
            }
    )
    public ResponseEntity<Map<String, Object>> getUsuarioLogado(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {

        Usuario usuario = usuarioRepository.findByUsernameOrEmail(userDetails.getUsername(), userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        UsuarioRespostaDto resposta = new UsuarioRespostaDto(
                usuario.getNome(),
                usuario.getUsername(),
                usuario.getEmail(),
                usuario.getFoto(),
                usuario.getCreationTimestamp()
        );

        Map<String, Object> respostaCompleta = new HashMap<>();
        respostaCompleta.put("id", usuario.getId());
        respostaCompleta.put("nome", usuario.getNome());
        respostaCompleta.put("username", usuario.getUsername());
        respostaCompleta.put("email", usuario.getEmail());
        respostaCompleta.put("foto", usuario.getFoto());
        respostaCompleta.put("creationTimestamp", usuario.getCreationTimestamp());

        return ResponseEntity.ok(respostaCompleta);
    }
}
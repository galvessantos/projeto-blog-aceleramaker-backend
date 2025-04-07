package aceleramaker.project.controller;

import aceleramaker.project.dto.CreatePostagemDto;
import aceleramaker.project.dto.PostagemRespostaDto;
import aceleramaker.project.dto.UpdatePostagemDto;
import aceleramaker.project.entity.Postagem;
import aceleramaker.project.exceptions.ResourceNotFoundException;
import aceleramaker.project.service.PostagemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/postagens")
@Tag(name = "03 - Postagens", description = "Operações relacionadas às postagens")
public class PostagemController {

    private final PostagemService postagemService;

    public PostagemController(PostagemService postagemService) {
        this.postagemService = postagemService;
    }

    @PostMapping
    @Operation(summary = "Criar uma nova postagem")
    public ResponseEntity<PostagemRespostaDto> criar(
            @RequestBody @Valid CreatePostagemDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        PostagemRespostaDto respostaDto = postagemService.criarDto(dto, userDetails.getUsername());
        return ResponseEntity.status(201).body(respostaDto);
    }

    @GetMapping
    @Operation(summary = "Listar todas as postagens ou buscar por título")
    public ResponseEntity<Page<PostagemRespostaDto>> listarTodas(
            @RequestParam(required = false) String titulo,
            @Parameter(hidden = true) Pageable pageable) {
        Page<PostagemRespostaDto> page = (titulo == null)
                ? postagemService.listarTodas(pageable)
                : postagemService.buscarPorTitulo(titulo, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar uma postagem por ID")
    public ResponseEntity<PostagemRespostaDto> buscarPorId(
            @Parameter(description = "ID da postagem") @PathVariable Long id) {

        PostagemRespostaDto respostaDto = postagemService.buscarPorIdDto(id)
                .orElseThrow(() -> new ResourceNotFoundException("Postagem não encontrada"));
        return ResponseEntity.ok(respostaDto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar uma postagem por ID")
    public ResponseEntity<PostagemRespostaDto> atualizar(
            @Parameter(description = "ID da postagem") @PathVariable Long id,
            @RequestBody @Valid UpdatePostagemDto dto) {

        PostagemRespostaDto respostaDto = postagemService.atualizarDto(id, dto)
                .orElseThrow(() -> new ResourceNotFoundException("Postagem não encontrada"));
        return ResponseEntity.ok(respostaDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar uma postagem por ID")
    public ResponseEntity<Map<String, String>> deletar(@PathVariable Long id) {
        postagemService.deletar(id);
        Map<String, String> resposta = Map.of("mensagem", "Postagem excluída com sucesso.");
        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/tema/{temaId}")
    @Operation(summary = "Buscar postagens por ID do tema")
    public ResponseEntity<Page<PostagemRespostaDto>> buscarPorTema(
            @Parameter(description = "ID do tema") @PathVariable Long temaId,
            @Parameter(hidden = true) Pageable pageable) {
        return ResponseEntity.ok(postagemService.buscarPorTema(temaId, pageable));
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Buscar postagens por ID do usuário")
    public ResponseEntity<Page<PostagemRespostaDto>> buscarPorUsuario(
            @Parameter(description = "ID do usuário") @PathVariable Long usuarioId,
            @Parameter(hidden = true) Pageable pageable) {
        return ResponseEntity.ok(postagemService.buscarPorUsuario(usuarioId, pageable));
    }
}

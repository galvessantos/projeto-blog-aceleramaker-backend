package aceleramaker.project.controller;

import aceleramaker.project.dto.CreatePostagemDto;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/postagens")
@Tag(name = "Postagens", description = "Operações relacionadas às postagens")
public class PostagemController {

    private final PostagemService postagemService;

    public PostagemController(PostagemService postagemService) {
        this.postagemService = postagemService;
    }

    @PostMapping
    @Operation(summary = "Criar uma nova postagem")
    public ResponseEntity<Postagem> criar(@RequestBody @Valid CreatePostagemDto dto) {
        Postagem novaPostagem = postagemService.criar(dto);
        return ResponseEntity.status(201).body(novaPostagem);
    }

    @GetMapping
    @Operation(summary = "Listar todas as postagens ou buscar por título")
    public ResponseEntity<Page<Postagem>> listarTodas(
            @Parameter(description = "Título parcial da postagem para busca") @RequestParam(required = false) String titulo,
            @Parameter(hidden = true) Pageable pageable) {
        Page<Postagem> resultado = (titulo != null && !titulo.isEmpty())
                ? postagemService.buscarPorTitulo(titulo, pageable)
                : postagemService.listarTodas(pageable);

        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar uma postagem por ID")
    public ResponseEntity<Postagem> buscarPorId(
            @Parameter(description = "ID da postagem") @PathVariable Long id) {
        Postagem postagem = postagemService.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Postagem não encontrada"));
        return ResponseEntity.ok(postagem);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar uma postagem por ID")
    public ResponseEntity<Postagem> atualizar(
            @Parameter(description = "ID da postagem") @PathVariable Long id,
            @RequestBody @Valid UpdatePostagemDto dto) {
        Postagem atualizada = postagemService.atualizar(id, dto)
                .orElseThrow(() -> new ResourceNotFoundException("Postagem não encontrada"));
        return ResponseEntity.ok(atualizada);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar uma postagem por ID")
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID da postagem") @PathVariable Long id) {
        postagemService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tema/{temaId}")
    @Operation(summary = "Buscar postagens por ID do tema")
    public ResponseEntity<Page<Postagem>> buscarPorTema(
            @Parameter(description = "ID do tema") @PathVariable Long temaId,
            @Parameter(hidden = true) Pageable pageable) {
        return ResponseEntity.ok(postagemService.buscarPorTema(temaId, pageable));
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Buscar postagens por ID do usuário")
    public ResponseEntity<Page<Postagem>> buscarPorUsuario(
            @Parameter(description = "ID do usuário") @PathVariable Long usuarioId,
            @Parameter(hidden = true) Pageable pageable) {
        return ResponseEntity.ok(postagemService.buscarPorUsuario(usuarioId, pageable));
    }
}

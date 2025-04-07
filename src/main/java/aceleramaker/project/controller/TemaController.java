package aceleramaker.project.controller;

import aceleramaker.project.dto.CreateTemaDto;
import aceleramaker.project.entity.Tema;
import aceleramaker.project.exceptions.ResourceNotFoundException;
import aceleramaker.project.service.TemaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/temas")
@Tag(name = "Temas", description = "Operações relacionadas aos temas das postagens")
public class TemaController {

    private final TemaService temaService;

    public TemaController(TemaService temaService) {
        this.temaService = temaService;
    }

    @PostMapping
    @Operation(summary = "Criar um novo tema")
    public ResponseEntity<Tema> criar(@RequestBody @Valid CreateTemaDto dto) {
        Tema novoTema = temaService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoTema);
    }

    @GetMapping
    @Operation(summary = "Listar todos os temas ou buscar por descrição parcial")
    public ResponseEntity<List<Tema>> listarTodos(
            @Parameter(description = "Descrição parcial para busca") @RequestParam(required = false) String descricao) {
        List<Tema> temas = (descricao != null && !descricao.isEmpty())
                ? temaService.buscarPorDescricaoParcial(descricao)
                : temaService.listarTodos();
        return ResponseEntity.ok(temas);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar um tema por ID")
    public ResponseEntity<Tema> buscarPorId(
            @Parameter(description = "ID do tema") @PathVariable Long id) {
        Tema tema = temaService.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tema não encontrado"));
        return ResponseEntity.ok(tema);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um tema por ID")
    public ResponseEntity<Tema> atualizar(
            @Parameter(description = "ID do tema") @PathVariable Long id,
            @RequestBody @Valid CreateTemaDto dto) {
        Tema temaAtualizado = temaService.atualizar(id, dto)
                .orElseThrow(() -> new ResourceNotFoundException("Tema não encontrado"));
        return ResponseEntity.ok(temaAtualizado);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar um tema por ID")
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID do tema") @PathVariable Long id) {
        temaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}

package aceleramaker.project.controller;

import aceleramaker.project.dto.CreateTemaDto;
import aceleramaker.project.dto.TemaRespostaDto;
import aceleramaker.project.entity.Tema;
import aceleramaker.project.exceptions.ResourceNotFoundException;
import aceleramaker.project.service.TemaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/temas")
@Tag(name = "04 - Temas", description = "Operações relacionadas aos temas das postagens")
public class TemaController {

    private final TemaService temaService;

    public TemaController(TemaService temaService) {
        this.temaService = temaService;
    }

    @PostMapping
    @Operation(summary = "Criar um novo tema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tema criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos para criação do tema")
    })
    public ResponseEntity<TemaRespostaDto> criar(@RequestBody @Valid CreateTemaDto dto) {
        Tema novoTema = temaService.criar(dto);
        TemaRespostaDto respostaDto = new TemaRespostaDto(novoTema.getId(), novoTema.getDescricao());
        return ResponseEntity.status(HttpStatus.CREATED).body(respostaDto);
    }

    @GetMapping
    @Operation(summary = "Listar todos os temas ou buscar por descrição parcial")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de temas retornada com sucesso")
    })
    public ResponseEntity<List<TemaRespostaDto>> listarTodos(
            @Parameter(description = "Descrição parcial para buscar temas") @RequestParam(required = false) String descricao) {
        List<Tema> temas = (descricao != null && !descricao.isEmpty())
                ? temaService.buscarPorDescricaoParcial(descricao)
                : temaService.listarTodos();

        List<TemaRespostaDto> resposta = temas.stream()
                .map(t -> new TemaRespostaDto(t.getId(), t.getDescricao()))
                .collect(toList());

        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar um tema por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tema encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Tema não encontrado")
    })
    public ResponseEntity<TemaRespostaDto> buscarPorId(
            @Parameter(description = "ID do tema") @PathVariable Long id) {
        Tema tema = temaService.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tema não encontrado"));
        TemaRespostaDto dto = new TemaRespostaDto(tema.getId(), tema.getDescricao());
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um tema por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tema atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos para atualização"),
            @ApiResponse(responseCode = "404", description = "Tema não encontrado")
    })
    public ResponseEntity<TemaRespostaDto> atualizar(
            @Parameter(description = "ID do tema") @PathVariable Long id,
            @RequestBody @Valid CreateTemaDto dto) {
        Tema temaAtualizado = temaService.atualizar(id, dto)
                .orElseThrow(() -> new ResourceNotFoundException("Tema não encontrado"));
        TemaRespostaDto respostaDto = new TemaRespostaDto(temaAtualizado.getId(), temaAtualizado.getDescricao());
        return ResponseEntity.ok(respostaDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar um tema por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tema deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Tema não encontrado")
    })
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID do tema") @PathVariable Long id) {
        temaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}

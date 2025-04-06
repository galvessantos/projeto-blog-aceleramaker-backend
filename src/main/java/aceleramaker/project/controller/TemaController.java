package aceleramaker.project.controller;

import aceleramaker.project.dto.CreateTemaDto;
import aceleramaker.project.entity.Tema;
import aceleramaker.project.service.TemaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/temas")
public class TemaController {

    private final TemaService temaService;

    public TemaController(TemaService temaService) {
        this.temaService = temaService;
    }

    @PostMapping
    public ResponseEntity<Tema> criar(@RequestBody CreateTemaDto dto) {
        Tema novoTema = temaService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoTema);
    }

    @GetMapping
    public ResponseEntity<List<Tema>> listarTodos(@RequestParam(required = false) String descricao) {
        List<Tema> temas = (descricao != null && !descricao.isEmpty())
                ? temaService.buscarPorDescricaoParcial(descricao)
                : temaService.listarTodos();
        return ResponseEntity.ok(temas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tema> buscarPorId(@PathVariable Long id) {
        Tema tema = temaService.buscarPorId(id)
                .orElseThrow(() -> new aceleramaker.project.exceptions.ResourceNotFoundException("Tema não encontrado"));
        return ResponseEntity.ok(tema);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tema> atualizar(@PathVariable Long id, @RequestBody CreateTemaDto dto) {
        Tema temaAtualizado = temaService.atualizar(id, dto)
                .orElseThrow(() -> new aceleramaker.project.exceptions.ResourceNotFoundException("Tema não encontrado"));
        return ResponseEntity.ok(temaAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        temaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}

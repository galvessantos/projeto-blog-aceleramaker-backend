package aceleramaker.project.controller;

import aceleramaker.project.dto.CreateTemaDto;
import aceleramaker.project.entity.Tema;
import aceleramaker.project.service.TemaService;
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
        return ResponseEntity.ok(temaService.criar(dto));
    }

    @GetMapping
    public ResponseEntity<List<Tema>> listarTodos(@RequestParam(required = false) String descricao) {
        if (descricao != null && !descricao.isEmpty()) {
            return ResponseEntity.ok(temaService.buscarPorDescricaoParcial(descricao));
        }
        return ResponseEntity.ok(temaService.listarTodos());
    }

}

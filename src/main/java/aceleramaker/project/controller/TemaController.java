package aceleramaker.project.controller;

import aceleramaker.project.dto.CreateTemaDto;
import aceleramaker.project.entity.Tema;
import aceleramaker.project.service.TemaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}

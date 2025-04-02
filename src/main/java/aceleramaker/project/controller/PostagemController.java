package aceleramaker.project.controller;

import aceleramaker.project.dto.CreatePostagemDto;
import aceleramaker.project.dto.UpdatePostagemDto;
import aceleramaker.project.entity.Postagem;
import aceleramaker.project.service.PostagemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/postagens")
public class PostagemController {

    private final PostagemService postagemService;

    public PostagemController(PostagemService postagemService) {
        this.postagemService = postagemService;
    }

    @PostMapping
    public ResponseEntity<Postagem> criar(@RequestBody CreatePostagemDto dto) {
        return ResponseEntity.ok(postagemService.criar(dto));
    }

    @GetMapping
    public ResponseEntity<List<Postagem>> listarTodas(@RequestParam(required = false) String titulo) {
        if (titulo != null && !titulo.isEmpty()) {
            return ResponseEntity.ok(postagemService.buscarPorTitulo(titulo));
        }
        return ResponseEntity.ok(postagemService.listarTodas());
    }


    @GetMapping("/{id}")
    public ResponseEntity<Postagem> buscarPorId(@PathVariable Long id) {
        return postagemService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Postagem> atualizar(@PathVariable Long id, @RequestBody UpdatePostagemDto dto) {
        return postagemService.atualizar(id, dto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

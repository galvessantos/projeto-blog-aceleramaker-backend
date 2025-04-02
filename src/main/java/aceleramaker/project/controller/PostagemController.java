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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        postagemService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tema/{temaId}")
    public ResponseEntity<List<Postagem>> buscarPorTema(@PathVariable Long temaId) {
        return ResponseEntity.ok(postagemService.buscarPorTema(temaId));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Postagem>> buscarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(postagemService.buscarPorUsuario(usuarioId));
    }
}

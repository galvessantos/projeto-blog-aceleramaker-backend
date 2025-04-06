package aceleramaker.project.controller;

import aceleramaker.project.dto.CreatePostagemDto;
import aceleramaker.project.dto.UpdatePostagemDto;
import aceleramaker.project.entity.Postagem;
import aceleramaker.project.service.PostagemService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/postagens")
public class PostagemController {

    private final PostagemService postagemService;

    public PostagemController(PostagemService postagemService) {
        this.postagemService = postagemService;
    }

    @PostMapping
    public ResponseEntity<Postagem> criar(@RequestBody CreatePostagemDto dto) {
        Postagem novaPostagem = postagemService.criar(dto);
        return ResponseEntity.status(201).body(novaPostagem);
    }

    @GetMapping
    public ResponseEntity<Page<Postagem>> listarTodas(@RequestParam(required = false) String titulo, Pageable pageable) {
        Page<Postagem> resultado = (titulo != null && !titulo.isEmpty())
                ? postagemService.buscarPorTitulo(titulo, pageable)
                : postagemService.listarTodas(pageable);

        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Postagem> buscarPorId(@PathVariable Long id) {
        Postagem postagem = postagemService.buscarPorId(id)
                .orElseThrow(() -> new aceleramaker.project.exceptions.ResourceNotFoundException("Postagem não encontrada"));
        return ResponseEntity.ok(postagem);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Postagem> atualizar(@PathVariable Long id, @RequestBody UpdatePostagemDto dto) {
        Postagem atualizada = postagemService.atualizar(id, dto)
                .orElseThrow(() -> new aceleramaker.project.exceptions.ResourceNotFoundException("Postagem não encontrada"));
        return ResponseEntity.ok(atualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        postagemService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tema/{temaId}")
    public ResponseEntity<Page<Postagem>> buscarPorTema(@PathVariable Long temaId, Pageable pageable) {
        return ResponseEntity.ok(postagemService.buscarPorTema(temaId, pageable));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Page<Postagem>> buscarPorUsuario(@PathVariable Long usuarioId, Pageable pageable) {
        return ResponseEntity.ok(postagemService.buscarPorUsuario(usuarioId, pageable));
    }
}

package aceleramaker.project.controller;

import aceleramaker.project.dto.CreateUsuarioDto;
import aceleramaker.project.dto.UpdateUsuarioDto;
import aceleramaker.project.entity.Usuario;
import aceleramaker.project.exceptions.ResourceNotFoundException;
import aceleramaker.project.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/v1/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<Usuario> createUser(@RequestBody CreateUsuarioDto createUsuarioDto) {
        var usuarioId = usuarioService.createUsuario(createUsuarioDto);
        return ResponseEntity.created(URI.create("/v1/usuarios/" + usuarioId)).build();
    }

    @GetMapping("/{usuarioId}")
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable String usuarioId) {
        var usuario = usuarioService.getUsuarioById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + usuarioId));
        return ResponseEntity.ok(usuario);
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listUsuarios() {
        var usuarios = usuarioService.listUsers();
        return ResponseEntity.ok(usuarios);
    }

    @PutMapping("/{usuarioId}")
    public ResponseEntity<Void> updateUsuarioById(@PathVariable String usuarioId,
                                                  @RequestBody UpdateUsuarioDto updateUsuarioDto) {
        usuarioService.updateUsuarioById(usuarioId, updateUsuarioDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{usuarioId}")
    public ResponseEntity<Void> deleteById(@PathVariable String usuarioId) {
        usuarioService.deleteById(usuarioId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/protected")
    public ResponseEntity<String> getProtectedEndpoint() {
        return ResponseEntity.ok("Você acessou um endpoint protegido!");
    }
}

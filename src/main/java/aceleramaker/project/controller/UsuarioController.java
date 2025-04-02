package aceleramaker.project.controller;


import aceleramaker.project.dto.CreateUsuarioDto;
import aceleramaker.project.entity.Usuario;
import aceleramaker.project.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/v1/usuarios")
public class UsuarioController {

    private UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<Usuario> createUser(@RequestBody CreateUsuarioDto createUsuarioDto) {
        var usuarioId = usuarioService.createUsuario(createUsuarioDto);
        return ResponseEntity.created(URI.create("/v1/usuarios/" + usuarioId.toString())).build();
    }

    @GetMapping("/{usuarioId}")
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable("usuarioId") String usuarioId) {
        var usuario = usuarioService.getUsuarioById(usuarioId);
        return usuario.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listUsuarios() {
        var usuarios = usuarioService.listUsers();

        return ResponseEntity.ok(usuarios);
    }
}

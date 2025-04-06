package aceleramaker.project.controller;

import aceleramaker.project.dto.LoginDto;
import aceleramaker.project.dto.LoginRespostaDto;
import aceleramaker.project.dto.CreateUsuarioDto;
import aceleramaker.project.service.UsuarioService;
import aceleramaker.project.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UsuarioService usuarioService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider, UsuarioService usuarioService) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginRespostaDto> login(@RequestBody LoginDto loginDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.login(),
                            loginDto.senha()
                    )
            );

            String token = tokenProvider.generateToken(authentication);

            return ResponseEntity.ok(new LoginRespostaDto(token));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(new LoginRespostaDto("Credenciais inválidas"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody CreateUsuarioDto createUsuarioDto) {
        try {
            var usuarioId = usuarioService.createUsuario(createUsuarioDto);
            return ResponseEntity.created(URI.create("/v1/usuarios/" + usuarioId.toString()))
                    .body("Usuário registrado com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao registrar usuário.");
        }
    }

}

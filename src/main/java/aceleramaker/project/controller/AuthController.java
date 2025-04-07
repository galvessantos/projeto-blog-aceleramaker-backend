package aceleramaker.project.controller;

import aceleramaker.project.dto.LoginDto;
import aceleramaker.project.dto.LoginRespostaDto;
import aceleramaker.project.dto.CreateUsuarioDto;
import aceleramaker.project.exceptions.BadRequestException;
import aceleramaker.project.security.JwtTokenProvider;
import aceleramaker.project.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "01 - Autenticação", description = "Endpoints de login e registro de usuário")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UsuarioService usuarioService;

    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider, UsuarioService usuarioService) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    @Operation(summary = "Realizar login",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Credenciais inválidas")
            }
    )
    public ResponseEntity<LoginRespostaDto> login(@RequestBody @Valid LoginDto loginDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.login(), loginDto.senha())
            );
            String token = tokenProvider.generateToken(authentication);
            return ResponseEntity.ok(new LoginRespostaDto(token));
        } catch (Exception e) {
            throw new BadRequestException("Credenciais inválidas");
        }
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar novo usuário",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Usuário registrado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Erro ao registrar usuário")
            }
    )
    public ResponseEntity<Map<String, String>> register(@RequestBody @Valid CreateUsuarioDto createUsuarioDto) {
        try {
            Long usuarioId = usuarioService.createUsuario(createUsuarioDto);
            Map<String, String> response = Map.of("message", "Usuário registrado com sucesso!");
            return ResponseEntity.created(URI.create("/v1/usuarios/" + usuarioId)).body(response);
        } catch (Exception e) {
            throw new BadRequestException("Erro ao registrar usuário: " + e.getMessage());
        }
    }
}

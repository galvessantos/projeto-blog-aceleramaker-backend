package aceleramaker.project.service;

import aceleramaker.project.dto.CreateUsuarioDto;
import aceleramaker.project.dto.UpdateUsuarioDto;
import aceleramaker.project.entity.Usuario;
import aceleramaker.project.enums.Role;
import aceleramaker.project.exceptions.ResourceNotFoundException;
import aceleramaker.project.repository.UsuarioRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Long createUsuario(CreateUsuarioDto dto) {
        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setUsername(dto.username());
        usuario.setEmail(dto.email());
        usuario.setSenha(passwordEncoder.encode(dto.senha()));
        usuario.setRole(Role.USER);
        return usuarioRepository.save(usuario).getId();
    }

    public Optional<Usuario> getUsuarioById(Long usuarioId) {
        return usuarioRepository.findById(usuarioId);
    }

    public List<Usuario> listUsers() {
        return usuarioRepository.findAll();
    }

    public void updateUsuarioById(Long usuarioId, UpdateUsuarioDto dto) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        String usernameLogado = getUsuarioLogadoUsername();

        if (!usuario.getUsername().equals(usernameLogado) && !isCurrentUserAdmin()) {
            throw new AccessDeniedException("Acesso negado: você só pode editar sua própria conta.");
        }

        usuario.setNome(dto.nome());

        if (dto.senha() != null && !dto.senha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(dto.senha()));
        }

        if (dto.foto() != null) {
            usuario.setFoto(dto.foto());
        }

        usuarioRepository.save(usuario);
    }

    public void deleteById(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        String usernameLogado = getUsuarioLogadoUsername();

        if (!usuario.getUsername().equals(usernameLogado) && usuario.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Acesso negado: você só pode excluir sua própria conta.");
        }

        usuarioRepository.deleteById(usuarioId);
    }

    private String getUsuarioLogadoUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private boolean isCurrentUserAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
    }

    public String salvarFoto(MultipartFile foto) {
        try {

            String originalFilename = foto.getOriginalFilename();
            String extensao = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
            String nomeArquivo = UUID.randomUUID() + extensao;

            String caminhoUpload = "uploads/fotos/";
            Path diretorioDestino = Paths.get(caminhoUpload);

            if (!Files.exists(diretorioDestino)) {
                Files.createDirectories(diretorioDestino);
            }

            Path caminhoArquivo = diretorioDestino.resolve(nomeArquivo);

            Files.copy(foto.getInputStream(), caminhoArquivo, StandardCopyOption.REPLACE_EXISTING);

            return caminhoUpload + nomeArquivo;
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar arquivo: " + e.getMessage());
        }
    }
}

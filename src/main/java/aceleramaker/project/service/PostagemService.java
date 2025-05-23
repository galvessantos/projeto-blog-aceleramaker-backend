package aceleramaker.project.service;

import aceleramaker.project.dto.CreatePostagemDto;
import aceleramaker.project.dto.PostagemRespostaDto;
import aceleramaker.project.dto.TemaRespostaDto;
import aceleramaker.project.dto.UsuarioRespostaDto;
import aceleramaker.project.dto.UpdatePostagemDto;
import aceleramaker.project.entity.Postagem;
import aceleramaker.project.entity.Tema;
import aceleramaker.project.entity.Usuario;
import aceleramaker.project.exceptions.AccessDeniedCustomException;
import aceleramaker.project.exceptions.ResourceNotFoundException;
import aceleramaker.project.repository.PostagemRepository;
import aceleramaker.project.repository.TemaRepository;
import aceleramaker.project.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PostagemService {

    private final PostagemRepository postagemRepository;
    private final TemaRepository temaRepository;
    private final UsuarioRepository usuarioRepository;

    public PostagemService(PostagemRepository postagemRepository, TemaRepository temaRepository, UsuarioRepository usuarioRepository) {
        this.postagemRepository = postagemRepository;
        this.temaRepository = temaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public Page<PostagemRespostaDto> listarTodas(Pageable pageable) {
        return postagemRepository.findAll(pageable).map(this::converterParaDto);
    }

    public Optional<Postagem> buscarPorId(Long id) {
        return postagemRepository.findById(id);
    }

    public Optional<PostagemRespostaDto> buscarPorIdDto(Long id) {
        return buscarPorId(id).map(this::converterParaDto);
    }

    public Page<PostagemRespostaDto> buscarPorTitulo(String titulo, Pageable pageable) {
        return postagemRepository.findByTituloContainingIgnoreCase(titulo, pageable).map(this::converterParaDto);
    }

    public Postagem criar(CreatePostagemDto dto, String usernameAutenticado) {
        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + dto.usuarioId()));
        if (!usuario.getUsername().equals(usernameAutenticado)) {
            throw new AccessDeniedCustomException("Você só pode criar postagens com seu próprio ID.");
        }
        Tema tema = temaRepository.findById(dto.temaId())
                .orElseThrow(() -> new ResourceNotFoundException("Tema não encontrado com ID: " + dto.temaId()));
        Postagem postagem = new Postagem();
        postagem.setTitulo(dto.titulo());
        postagem.setTexto(dto.texto());
        postagem.setTema(tema);
        postagem.setUsuario(usuario);
        return postagemRepository.save(postagem);
    }

    public PostagemRespostaDto criarDto(CreatePostagemDto dto, String usernameAutenticado) {
        Postagem postagem = criar(dto, usernameAutenticado);
        return converterParaDto(postagem);
    }

    public Optional<Postagem> atualizar(Long id, UpdatePostagemDto dto) {
        return postagemRepository.findById(id).map(postagem -> {
            postagem.setTitulo(dto.titulo());
            postagem.setTexto(dto.texto());
            if (dto.temaId() != null) {
                Tema tema = temaRepository.findById(dto.temaId())
                        .orElseThrow(() -> new ResourceNotFoundException("Tema não encontrado com ID: " + dto.temaId()));
                postagem.setTema(tema);
            }
            return postagemRepository.save(postagem);
        });
    }

    public Optional<PostagemRespostaDto> atualizarDto(Long id, UpdatePostagemDto dto) {
        return atualizar(id, dto).map(this::converterParaDto);
    }

    public void deletar(Long id) {
        if (!postagemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Postagem não encontrada com ID: " + id);
        }
        postagemRepository.deleteById(id);
    }

    public Page<PostagemRespostaDto> buscarPorTema(Long temaId, Pageable pageable) {
        return postagemRepository.findByTemaId(temaId, pageable).map(this::converterParaDto);
    }

    public Page<PostagemRespostaDto> buscarPorUsuario(Long usuarioId, Pageable pageable) {
        return postagemRepository.findByUsuarioId(usuarioId, pageable).map(this::converterParaDto);
    }

    private PostagemRespostaDto converterParaDto(Postagem postagem) {
        return new PostagemRespostaDto(
                postagem.getId(),
                postagem.getTitulo(),
                postagem.getTexto(),
                new TemaRespostaDto(postagem.getTema().getId(), postagem.getTema().getDescricao()),
                new UsuarioRespostaDto(
                        postagem.getUsuario().getId(),
                        postagem.getUsuario().getNome(),
                        postagem.getUsuario().getUsername(),
                        postagem.getUsuario().getEmail(),
                        postagem.getUsuario().getFoto(),
                        postagem.getUsuario().getCreationTimestamp()
                ),
                postagem.getCreationTimestamp(),
                postagem.getUpdateTimestamp()
        );
    }
}

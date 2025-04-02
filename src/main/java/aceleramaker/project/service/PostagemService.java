package aceleramaker.project.service;

import aceleramaker.project.entity.Postagem;
import aceleramaker.project.repository.PostagemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PostagemService {

    @Autowired
    private PostagemRepository postagemRepository;

    public List<Postagem> listarTodas() {
        return postagemRepository.findAll();
    }

    public Optional<Postagem> buscarPorId(Long id) {
        return postagemRepository.findById(id);
    }

    public List<Postagem> buscarPorTitulo(String titulo) {
        return postagemRepository.findByTituloContainingIgnoreCase(titulo);
    }
}

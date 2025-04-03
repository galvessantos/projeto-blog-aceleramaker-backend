package aceleramaker.project.service;

import aceleramaker.project.dto.CreateTemaDto;
import aceleramaker.project.entity.Tema;
import aceleramaker.project.repository.TemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TemaService {

    @Autowired
    private TemaRepository temaRepository;

    public Tema criar(CreateTemaDto dto) {
        Tema tema = new Tema();
        tema.setDescricao(dto.descricao());
        return temaRepository.save(tema);
    }

    public List<Tema> listarTodos() {
        return temaRepository.findAll();
    }

    public Optional<Tema> atualizar(Long id, CreateTemaDto dto) {
        return temaRepository.findById(id).map(tema -> {
            tema.setDescricao(dto.descricao());
            return temaRepository.save(tema);
        });
    }

    public void deletar(Long id) {
        if (!temaRepository.existsById(id)) {
            throw new RuntimeException("Tema não encontrado");
        }
        temaRepository.deleteById(id);
    }

    public Optional<Tema> buscarPorId(Long id) {
        return temaRepository.findById(id);
    }

    public List<Tema> buscarPorDescricaoParcial(String descricao) {
        return temaRepository.findByDescricaoContainingIgnoreCase(descricao);
    }

}

package aceleramaker.project.service;

import aceleramaker.project.dto.CreateTemaDto;
import aceleramaker.project.entity.Tema;
import aceleramaker.project.repository.TemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
}

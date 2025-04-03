package aceleramaker.project.repository;

import aceleramaker.project.entity.Tema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TemaRepository extends JpaRepository<Tema, Long> {
    List<Tema> findByDescricaoContainingIgnoreCase(String descricao);
}

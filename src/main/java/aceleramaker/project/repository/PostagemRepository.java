package aceleramaker.project.repository;

import aceleramaker.project.entity.Postagem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostagemRepository extends JpaRepository<Postagem, Long> {
    Page<Postagem> findByTituloContainingIgnoreCase(String titulo, Pageable pageable);
    Page<Postagem> findByTemaId(Long temaId, Pageable pageable);
    Page<Postagem> findByUsuarioId(Long usuarioId, Pageable pageable);
}

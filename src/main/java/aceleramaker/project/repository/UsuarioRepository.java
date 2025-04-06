package aceleramaker.project.repository;

import aceleramaker.project.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    @Query("SELECT u FROM Usuario u WHERE u.username = :username OR u.email = :email")
    Optional<Usuario> findByUsernameOrEmail(@Param("username") String username, @Param("email") String email);
}


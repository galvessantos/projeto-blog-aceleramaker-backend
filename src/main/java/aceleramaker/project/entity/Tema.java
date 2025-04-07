package aceleramaker.project.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "tb_temas")
@Schema(description = "Entidade que representa um tema de postagens no blog.")
public class Tema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único do tema.", example = "1")
    private Long id;

    @Column(name = "descricao", nullable = false)
    @Schema(description = "Descrição do tema.", example = "Tecnologia")
    private String descricao;

    @OneToMany(mappedBy = "tema", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @Schema(description = "Lista de postagens associadas a este tema.")
    private List<Postagem> postagens;

    public Tema() {
    }

    public Tema(String descricao) {
        this.descricao = descricao;
    }

    public Tema(Long id, String descricao, List<Postagem> postagens) {
        this.id = id;
        this.descricao = descricao;
        this.postagens = postagens;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<Postagem> getPostagens() {
        return postagens;
    }

    public void setPostagens(List<Postagem> postagens) {
        this.postagens = postagens;
    }
}

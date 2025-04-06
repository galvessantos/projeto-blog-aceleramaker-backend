package aceleramaker.project.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "tb_temas")
public class Tema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "descricao", nullable = false)
    private String descricao;

    @OneToMany(mappedBy = "tema", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
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

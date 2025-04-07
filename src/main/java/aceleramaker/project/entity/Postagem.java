package aceleramaker.project.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_postagens")
@Schema(description = "Entidade que representa uma postagem no blog.")
public class Postagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único da postagem.", example = "1")
    private Long id;

    @Column(name = "titulo", nullable = false)
    @Schema(description = "Título da postagem.", example = "O melhor projeto do Acelera Maker")
    private String titulo;

    @Column(name = "texto", nullable = false, columnDefinition = "TEXT")
    @Schema(description = "Conteúdo da postagem.", example = "Neste post vamos analisar o código do Gabriel Alves...")
    private String texto;

    @ManyToOne
    @JoinColumn(name = "tema_id", nullable = false)
    @JsonIgnoreProperties("postagens")
    @Schema(description = "Tema associado à postagem.")
    private Tema tema;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIgnoreProperties("postagens")
    @Schema(description = "Usuário autor da postagem.")
    private Usuario usuario;

    @CreationTimestamp
    @Schema(description = "Data e hora de criação da postagem.")
    private LocalDateTime creationTimestamp;

    @UpdateTimestamp
    @Schema(description = "Data e hora da última atualização da postagem.")
    private LocalDateTime updateTimestamp;

    public Postagem() {
    }

    public Postagem(Long id, String titulo, String texto, Tema tema, Usuario usuario, LocalDateTime creationTimestamp, LocalDateTime updateTimestamp) {
        this.id = id;
        this.titulo = titulo;
        this.texto = texto;
        this.tema = tema;
        this.usuario = usuario;
        this.creationTimestamp = creationTimestamp;
        this.updateTimestamp = updateTimestamp;
    }

    public Postagem(String s, String s1, Tema tema, Usuario usuario) {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public Tema getTema() {
        return tema;
    }

    public void setTema(Tema tema) {
        this.tema = tema;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(LocalDateTime creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public LocalDateTime getUpdateTimestamp() {
        return updateTimestamp;
    }

    public void setUpdateTimestamp(LocalDateTime updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
    }
}

package aceleramaker.project.entity;

import aceleramaker.project.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Schema(description = "Entidade que representa um usuário da aplicação")
@Entity
@Table(name = "tb_users")
public class Usuario implements UserDetails {

    @Schema(description = "Papel do usuário (USER ou ADMIN)", example = "USER")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Schema(description = "Identificador único do usuário", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Nome completo do usuário", example = "João Silva")
    @Column(name = "nome", nullable = false)
    private String nome;

    @Schema(description = "Nome de usuário (login)", example = "joaosilva")
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Schema(description = "Email do usuário", example = "joao@email.com")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Schema(description = "Senha do usuário", example = "senha123")
    @Column(name = "senha", nullable = false)
    private String senha;

    @Schema(description = "URL da foto do usuário", example = "https://example.com/foto.jpg")
    @Column(name = "foto")
    private String foto;

    @Schema(description = "Lista de postagens associadas ao usuário")
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Postagem> postagens;

    @Schema(description = "Data de criação do usuário")
    @CreationTimestamp
    private Instant creationTimestamp;

    @Schema(description = "Data da última atualização do usuário")
    @UpdateTimestamp
    private Instant updateTimestamp;

    public Usuario(Long id, String nome, String username, String email, String senha, String foto, List<Postagem> postagens, Instant creationTimestamp, Instant updateTimestamp, Role role) {
        this.id = id;
        this.nome = nome;
        this.username = username;
        this.email = email;
        this.senha = senha;
        this.foto = foto;
        this.postagens = postagens;
        this.creationTimestamp = creationTimestamp;
        this.updateTimestamp = updateTimestamp;
        this.role = role;
    }

    public Usuario() {
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public List<Postagem> getPostagens() {
        return postagens;
    }

    public void setPostagens(List<Postagem> postagens) {
        this.postagens = postagens;
    }

    public Instant getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(Instant creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public Instant getUpdateTimestamp() {
        return updateTimestamp;
    }

    public void setUpdateTimestamp(Instant updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
    }
}

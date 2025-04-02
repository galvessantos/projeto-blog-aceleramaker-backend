package aceleramaker.project.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdatePostagemDto {

    @NotBlank
    private String titulo;

    @NotBlank
    private String texto;

    private Long temaId;

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

    public Long getTemaId() {
        return temaId;
    }

    public void setTemaId(Long temaId) {
        this.temaId = temaId;
    }
}

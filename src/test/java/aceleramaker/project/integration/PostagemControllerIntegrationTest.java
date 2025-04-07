package aceleramaker.project.integration;

import aceleramaker.project.dto.CreatePostagemDto;
import aceleramaker.project.dto.UpdatePostagemDto;
import aceleramaker.project.entity.Postagem;
import aceleramaker.project.entity.Tema;
import aceleramaker.project.entity.Usuario;
import aceleramaker.project.enums.Role;
import aceleramaker.project.repository.PostagemRepository;
import aceleramaker.project.repository.TemaRepository;
import aceleramaker.project.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PostagemControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostagemRepository postagemRepository;

    @Autowired
    private TemaRepository temaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;
    private Usuario usuario;
    private Tema tema;

    @BeforeEach
    void setUp() throws Exception {
        postagemRepository.deleteAll();
        temaRepository.deleteAll();
        usuarioRepository.deleteAll();

        Usuario admin = new Usuario();
        admin.setNome("Admin");
        admin.setUsername("admin");
        admin.setEmail("admin@email.com");
        admin.setSenha(passwordEncoder.encode("123456"));
        admin.setRole(Role.ADMIN);
        usuarioRepository.save(admin);

        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\":\"admin@email.com\",\"senha\":\"123456\"}"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode loginJson = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        token = loginJson.get("token").asText();

        usuario = admin;
        tema = temaRepository.save(new Tema("Tecnologia"));
    }

    @Test
    void deveCriarPostagem() throws Exception {
        CreatePostagemDto dto = new CreatePostagemDto("Título Teste","Texto Teste", tema.getId(), usuario.getId());
        mockMvc.perform(post("/postagens")
                        .header("Authorization","Bearer "+token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("Título Teste")));
    }

    @Test
    void deveListarTodasPostagens() throws Exception {
        postagemRepository.save(new Postagem(null,"Post 1","Texto",tema,usuario,null,null));
        postagemRepository.save(new Postagem(null,"Post 2","Texto",tema,usuario,null,null));
        mockMvc.perform(get("/postagens?page=0&size=10")
                        .header("Authorization","Bearer "+token))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Post 1")))
                .andExpect(content().string(containsString("Post 2")));
    }

    @Test
    void deveBuscarPostagemPorId() throws Exception {
        Postagem p = postagemRepository.save(new Postagem(null,"Título","Texto",tema,usuario,null,null));
        mockMvc.perform(get("/postagens/"+p.getId())
                        .header("Authorization","Bearer "+token))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Título")));
    }

    @Test
    void deveAtualizarPostagem() throws Exception {
        Postagem p = postagemRepository.save(new Postagem(null,"Antigo","Texto",tema,usuario,null,null));
        UpdatePostagemDto dto = new UpdatePostagemDto("Novo","Novo Texto", tema.getId());
        mockMvc.perform(put("/postagens/"+p.getId())
                        .header("Authorization","Bearer "+token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Novo"));
    }

    @Test
    void deveDeletarPostagem() throws Exception {
        Postagem p = postagemRepository.save(new Postagem(null,"Pra deletar","Texto",tema,usuario,null,null));
        mockMvc.perform(delete("/postagens/"+p.getId())
                        .header("Authorization","Bearer "+token))
                .andExpect(status().isOk());
    }

    @Test
    void deveBuscarPorTitulo() throws Exception {
        postagemRepository.save(new Postagem(null,"UniqueTitle","Texto",tema,usuario,null,null));
        mockMvc.perform(get("/postagens?titulo=UniqueTitle")
                        .header("Authorization","Bearer "+token))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("UniqueTitle")));
    }

    @Test
    void deveBuscarPorTema() throws Exception {
        postagemRepository.save(new Postagem(null,"PorTema","Texto",tema,usuario,null,null));
        mockMvc.perform(get("/postagens/tema/"+tema.getId())
                        .header("Authorization","Bearer "+token))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("PorTema")));
    }

    @Test
    void deveBuscarPorUsuario() throws Exception {
        postagemRepository.save(new Postagem(null,"PorUsuario","Texto",tema,usuario,null,null));
        mockMvc.perform(get("/postagens/usuario/"+usuario.getId())
                        .header("Authorization","Bearer "+token))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("PorUsuario")));
    }
}

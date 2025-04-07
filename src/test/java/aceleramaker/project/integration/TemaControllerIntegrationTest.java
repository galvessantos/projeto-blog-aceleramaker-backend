package aceleramaker.project.integration;

import aceleramaker.project.dto.CreateTemaDto;
import aceleramaker.project.dto.UpdateTemaDto;
import aceleramaker.project.entity.Tema;
import aceleramaker.project.entity.Usuario;
import aceleramaker.project.enums.Role;
import aceleramaker.project.repository.PostagemRepository;
import aceleramaker.project.repository.TemaRepository;
import aceleramaker.project.repository.UsuarioRepository;
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
class TemaControllerIntegrationTest {

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

        String loginPayload = """
        {
          "login": "admin@email.com",
          "senha": "123456"
        }
        """;

        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginPayload))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = loginResult.getResponse().getContentAsString();
        this.token = objectMapper.readTree(responseBody).get("token").asText();
    }

    @Test
    void deveCriarTema() throws Exception {
        CreateTemaDto dto = new CreateTemaDto("Tecnologia");

        mockMvc.perform(post("/temas")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("Tecnologia")));
    }

    @Test
    void deveBuscarTodosTemas() throws Exception {
        temaRepository.save(new Tema("Educação"));

        mockMvc.perform(get("/temas")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Educação")));
    }

    @Test
    void deveAtualizarTema() throws Exception {
        Tema tema = temaRepository.save(new Tema("Saúde"));

        UpdateTemaDto dto = new UpdateTemaDto("Saúde e Bem-estar");

        mockMvc.perform(put("/temas/" + tema.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Saúde e Bem-estar")));
    }

    @Test
    void deveDeletarTema() throws Exception {
        Tema tema = temaRepository.save(new Tema("Esporte"));

        mockMvc.perform(delete("/temas/" + tema.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }
}

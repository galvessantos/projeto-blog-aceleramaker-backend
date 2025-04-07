package aceleramaker.project.integration;

import aceleramaker.project.dto.CreateUsuarioDto;
import aceleramaker.project.dto.UpdateUsuarioDto;
import aceleramaker.project.entity.Usuario;
import aceleramaker.project.enums.Role;
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
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UsuarioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;

    @BeforeEach
    void setUp() throws Exception {
        usuarioRepository.deleteAll();
        Usuario admin = new Usuario();
        admin.setNome("Admin");
        admin.setUsername("admin");
        admin.setEmail("admin@email.com");
        admin.setSenha(passwordEncoder.encode("123456"));
        admin.setRole(Role.ADMIN);
        usuarioRepository.save(admin);
        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content("{\"login\":\"admin@email.com\",\"senha\":\"123456\"}"))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode loginJson = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        adminToken = loginJson.get("token").asText();
    }

    @Test
    void deveBuscarUsuarioPorId() throws Exception {
        Usuario u = new Usuario();
        u.setNome("João");
        u.setUsername("joao");
        u.setEmail("joao@email.com");
        u.setSenha(passwordEncoder.encode("senha123"));
        u.setRole(Role.USER);
        usuarioRepository.save(u);
        mockMvc.perform(get("/v1/usuarios/"+u.getId())
                        .header("Authorization","Bearer "+adminToken))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("João")));
    }

    @Test
    void deveListarUsuarios() throws Exception {
        Usuario u1 = new Usuario();
        u1.setNome("João");
        u1.setUsername("joao");
        u1.setEmail("joao@email.com");
        u1.setSenha(passwordEncoder.encode("senha123"));
        u1.setRole(Role.USER);
        usuarioRepository.save(u1);
        Usuario u2 = new Usuario();
        u2.setNome("Maria");
        u2.setUsername("maria");
        u2.setEmail("maria@email.com");
        u2.setSenha(passwordEncoder.encode("senha123"));
        u2.setRole(Role.USER);
        usuarioRepository.save(u2);
        mockMvc.perform(get("/v1/usuarios")
                        .header("Authorization","Bearer "+adminToken))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("João")))
                .andExpect(content().string(containsString("Maria")));
    }

    @Test
    void deveAtualizarUsuario() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setNome("João");
        usuario.setUsername("joao@email.com");
        usuario.setEmail("joao@email.com");
        usuario.setSenha(passwordEncoder.encode("senha123"));
        usuario.setRole(Role.USER);
        usuarioRepository.save(usuario);

        String token = login("joao@email.com", "senha123");

        String json = """
        {
            "nome": "João Atualizado",
            "username": "joaoatualizado@email.com",
            "senha": "senha456",
            "foto": null
        }
        """;

        mockMvc.perform(put("/v1/usuarios/" + usuario.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(json))
                .andExpect(status().isNoContent());
    }


    @Test
    void deveDeletarProprioUsuario() throws Exception {
        Usuario u = new Usuario();
        u.setNome("João");
        u.setUsername("joao");
        u.setEmail("joao@email.com");
        u.setSenha(passwordEncoder.encode("senha123"));
        u.setRole(Role.USER);
        usuarioRepository.save(u);
        MvcResult lr = mockMvc.perform(post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content("{\"login\":\""+u.getEmail()+"\",\"senha\":\"senha123\"}"))
                .andExpect(status().isOk())
                .andReturn();
        String userToken = objectMapper.readTree(lr.getResponse().getContentAsString()).get("token").asText();
        mockMvc.perform(delete("/v1/usuarios/"+u.getId())
                        .header("Authorization","Bearer "+userToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveNegarDeletarOutroUsuario() throws Exception {
        Usuario u1 = new Usuario();
        u1.setNome("João");
        u1.setUsername("joao");
        u1.setEmail("joao@email.com");
        u1.setSenha(passwordEncoder.encode("senha123"));
        u1.setRole(Role.USER);
        usuarioRepository.save(u1);
        Usuario u2 = new Usuario();
        u2.setNome("Maria");
        u2.setUsername("maria");
        u2.setEmail("maria@email.com");
        u2.setSenha(passwordEncoder.encode("senha123"));
        u2.setRole(Role.USER);
        usuarioRepository.save(u2);
        MvcResult lr = mockMvc.perform(post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content("{\"login\":\""+u1.getEmail()+"\",\"senha\":\"senha123\"}"))
                .andExpect(status().isOk())
                .andReturn();
        String userToken = objectMapper.readTree(lr.getResponse().getContentAsString()).get("token").asText();
        mockMvc.perform(delete("/v1/usuarios/"+u2.getId())
                        .header("Authorization","Bearer "+userToken))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Acesso negado: você não tem permissão para acessar este recurso."));

    }

    private String login(String login, String senha) throws Exception {
        String json = """
        {
            "login": "%s",
            "senha": "%s"
        }
        """.formatted(login, senha);

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        return new ObjectMapper().readTree(responseBody).get("token").asText();
    }

}

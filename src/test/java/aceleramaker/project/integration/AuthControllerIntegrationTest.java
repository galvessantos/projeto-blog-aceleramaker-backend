package aceleramaker.project.integration;

import aceleramaker.project.dto.CreateUsuarioDto;
import aceleramaker.project.dto.LoginDto;
import aceleramaker.project.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @BeforeEach
    void setup() throws Exception {
        usuarioRepository.deleteAll();

        CreateUsuarioDto createUsuarioDto = new CreateUsuarioDto("Novo User", "novoUser", "novo@email.com", "senha123");
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUsuarioDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Usuário registrado com sucesso!"));

        realizarLogin("novo@email.com", "senha123");
    }

    private String realizarLogin(String login, String senha) throws Exception {
        LoginDto loginDto = new LoginDto(login, senha);
        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode loginJson = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        return loginJson.get("token").asText();
    }

    @Test
    void deveRealizarLoginComCredenciaisValidas() throws Exception {
        LoginDto loginDto = new LoginDto("novo@email.com", "senha123");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void deveFalharLoginComCredenciaisInvalidas() throws Exception {
        LoginDto loginDto = new LoginDto("novo@email.com", "senhaIncorreta");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Credenciais inválidas"));
    }

    @Test
    void deveRegistrarNovoUsuarioComSucesso() throws Exception {
        CreateUsuarioDto createUsuarioDto = new CreateUsuarioDto("Outro User", "outroUser", "outro@email.com", "123456");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUsuarioDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Usuário registrado com sucesso!"));
    }
}

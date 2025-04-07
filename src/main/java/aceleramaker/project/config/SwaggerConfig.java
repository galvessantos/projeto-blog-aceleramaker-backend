package aceleramaker.project.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        List<Tag> tags = Arrays.asList(
                new Tag().name("01 - Autenticação").description("Endpoints de login e registro de usuário"),
                new Tag().name("02 - Usuários").description("Operações relacionadas à conta do usuário"),
                new Tag().name("03 - Postagens").description("Operações relacionadas às postagens"),
                new Tag().name("04 - Temas").description("Operações relacionadas aos temas das postagens")
        );

        return new OpenAPI()
                .info(new Info()
                        .title("Blog Pessoal API")
                        .version("v1")
                        .description("API do Projeto Blog Pessoal - Acelera Maker | Montreal | @Gabriel Alves"))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .tags(tags);
    }
}
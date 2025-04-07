package aceleramaker.project.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Blog Pessoal API")
                        .version("v1")
                        .description("API do Projeto Blog Pessoal - Acelera Maker | Montreal | @Gabriel Alves")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org"))
                        .contact(new Contact()
                                .name("Gabriel Alves")
                                .email("gabrielww1@hotmail.com")
                                .url("https://github.com/galvessantos")));
    }
}
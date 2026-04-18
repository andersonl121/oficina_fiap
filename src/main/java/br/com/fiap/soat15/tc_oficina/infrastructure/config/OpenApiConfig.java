package br.com.fiap.soat15.tc_oficina.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Oficina Mecânica API")
                        .description("Sistema Integrado de Atendimento e Execução de Serviços")
                        .version("1.0.0"));
    }
}

package com.squad13.apimonolito.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI apiMonolitoOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API CRUD de Especificações - Squad 13")
                        .description("Documentação da API de Especificações da Jotanunes Construtora")
                        .version("v1.0"));
    }
}

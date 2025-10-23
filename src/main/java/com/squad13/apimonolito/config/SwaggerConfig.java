package com.squad13.apimonolito.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public GroupedOpenApi apiMonolitoAuth() {
        return GroupedOpenApi.builder()
                .group("auth-public")
                .pathsToMatch("/api/user/**")
                .build();
    }

    @Bean
    public GroupedOpenApi apiMonolitoCatalog() {
        return GroupedOpenApi.builder()
                .group("catalog-public")
                .pathsToMatch("/api/catalogo/**")
                .build();
    }

    @Bean
    public GroupedOpenApi apiMonolitoEditor() {
        return GroupedOpenApi.builder()
                .group("editor-public")
                .pathsToMatch("/api/editor/**")
                .build();
    }

    @Bean
    public OpenAPI authOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Especificaçãoes - Squad 13")
                        .description("Documentação da API de Especificações da Jotanunes Construtora")
                        .version("v1.0"))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}

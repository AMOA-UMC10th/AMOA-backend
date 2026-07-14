package com.amoa.server.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "BearerAuth";

    @Bean
    public OpenAPI swagger() {
        Info info = new Info()
                .title("amoa")
                .description("amoa API 명세서")
                .version("0.0.1"
                );

        Components components = new Components()
                .addSecuritySchemes(
                        SECURITY_SCHEME_NAME,
                        new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                );

        SecurityRequirement securityRequirement =
                new SecurityRequirement().addList(SECURITY_SCHEME_NAME);

        return new OpenAPI()
                .info(info)
                .addServersItem(new Server().url("/"))
                .components(components)
                .addSecurityItem(securityRequirement);
    }
}

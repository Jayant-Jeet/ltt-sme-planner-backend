package com.edulearnorg.ltt.smeplanner.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "L&TT Planner API",
        version = "v1.0.0",
        description = "L&TT Planner Backend API for managing user schedules and availability with role-based access control (SME, Supervisor, Lead)",
        termsOfService = "https://www.edulearnorg.com/terms-of-service",
        contact = @Contact(
            name = "L&TT Planner Team",
            email = "ltt-planner-support@edulearnorg.com",
            url = "https://www.edulearnorg.com"
        ),
        license = @License(
            name = "Proprietary License",
            url = "https://www.edulearnorg.com/license"
        )
    ),
    servers = {
        @Server(
            description = "Local Development Server",
            url = "http://localhost:8080"
        ),
        @Server(
            description = "Production Server",
            url = "https://api.sme-planner.edulearnorg.com"
        )
    }
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer",
    description = "JWT Authentication token required for secure endpoints"
)
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components());
    }
}

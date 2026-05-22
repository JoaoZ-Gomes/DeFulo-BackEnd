package com.defulo.api.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração OpenAPI / Swagger para a API DeFulo.
 * Define metadados, segurança JWT e esquemas da documentação.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        
        return new OpenAPI()
                .info(new Info()
                        .title("DeFulo Backend API")
                        .version("1.0.0")
                        .description(
                                "API de gestão de inteligência rural. " +
                                "Capacita engenheiros agrônomos a gerenciar múltiplas fazendas, talhões e eventos de manejo. " +
                                "Suporta operação offline-first com sincronização posterior.\n\n" +
                                "**Fluxo de autenticação:**\n" +
                                "1. Realize login em `POST /api/auth/login`\n" +
                                "2. Copie o token JWT da resposta\n" +
                                "3. Clique no botão 'Authorize' acima e insira: `Bearer <seu-token>`\n" +
                                "4. Acesse endpoints protegidos normalmente\n\n" +
                                "**Credenciais de teste:**\n" +
                                "- Email: `admin@defulo.com` / Senha: `admin123`\n" +
                                "- Email: `produtor@defulo.com` / Senha: `demo`"
                        )
                        .contact(new Contact()
                                .name("DeFulo Team")
                                .email("support@defulo.com")
                                .url("https://defulo.com")
                        )
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Insira o token JWT retornado pelo endpoint de login")
                        )
                );
    }
}

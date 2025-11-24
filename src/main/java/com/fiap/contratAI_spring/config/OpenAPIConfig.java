package com.fiap.contratAI_spring.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI contratAIOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ContratAI API")
                        .description("API REST para análise e geração de contratos utilizando Inteligência Artificial (Google Gemini). " +
                                "Este sistema permite que usuários registrados (Clientes, Advogados e Administradores) " +
                                "possam gerar contratos automaticamente com base em termos fornecidos e analisar contratos existentes " +
                                "identificando cláusulas críticas e sugerindo melhorias. " +
                                "\n\n**Características principais:**\n" +
                                "- Geração automática de contratos usando IA\n" +
                                "- Análise assíncrona de contratos via RabbitMQ\n" +
                                "- Sistema de autenticação e autorização baseado em roles\n" +
                                "- Integração com Google Gemini AI\n" +
                                "- Paginação de resultados\n" +
                                "- Banco de dados Oracle\n" +
                                "\n\n**Autenticação:**\n" +
                                "A API utiliza autenticação HTTP Basic. Para acessar os endpoints protegidos, " +
                                "forneça as credenciais no formato: `username:password` (Base64 encoded) no header Authorization.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("FIAP - Global Solution Team")
                                .email("contato@contratai.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor de Desenvolvimento Local"),
                        new Server()
                                .url("https://api.contratai.com")
                                .description("Servidor de Produção")))
                .addSecurityItem(new SecurityRequirement().addList("basicAuth"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("basicAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("basic")
                                        .description("Autenticação HTTP Basic. Use suas credenciais de usuário registrado.")));
    }
}

package com.aurora.ledger.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI Configuration  Professional Banking API Documentation
 * Implements enterprisegrade API documentation following banking standards
 * 
 * Features:
 *  JWT Authentication Security Scheme
 *  Comprehensive API metadata
 *  CQRSaware endpoint documentation
 *  Banking industry compliance information
 */
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Aurora Ledger Banking API",
        description = """
            Minimal OpenAPI definition for Aurora Ledger endpoints.
            Scope: authentication, accounts, transactions, balance, health.
            Patterns: CQRS (read/write separation), basic idempotency via Idempotency-Key header.
            Security: JWT bearer (HS512) with 24h expiration (demo), no PII in responses beyond masked identifiers.
            Compliance references intentionally omitted until supporting artifacts exist.
            """,
        version = "1.0.0",
        contact = @Contact(
            name = "CEZI COLA Senior Software Engineer",
            email = "contact@enterprise.com",
            url = "https://github.com/razecmarketing/auroraledgersantbank"
        ),
        license = @License(
            name = "Custom License",
            url = "https://example.com/license"
        )
    ),
    servers = {
        @Server(url = "https://api.auroraledger.com.br", description = "Production Server  Santander Environment"),
        @Server(url = "https://stagingapi.auroraledger.com.br", description = "Staging Server"),
        @Server(url = "http://localhost:8080", description = "Local Development Server")
    }
)
@SecurityScheme(
    name = "JWT Authentication",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = """
        JWT Bearer token authentication.
        
        **How to authenticate:**
        1. POST to /api/auth/login with credentials
        2. Extract the 'token' field from response
        3. Include in Authorization header: `Bearer {token}`
        
        **Token contains:**
         User identification
         Expiration time
         Banking permissions
        
        **Security Features:**
         Tokens expire after 24 hours
         Cryptographically signed with HS512
         Cannot be tampered with
        """
)
public class OpenApiConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new io.swagger.v3.oas.models.info.Info()
                .title("Aurora Ledger Banking API")
                .version("1.0.0")
                .description("Enterprise Banking API with CQRS Architecture")
                .contact(new io.swagger.v3.oas.models.info.Contact()
                    .name("CEZI COLA Senior Software Engineer")
                    .email("contact@enterprise.com"))
                .license(new io.swagger.v3.oas.models.info.License()
                    .name("Proprietary Banking License")))
            .addSecurityItem(new SecurityRequirement()
                .addList("JWT Authentication"));
    }
}











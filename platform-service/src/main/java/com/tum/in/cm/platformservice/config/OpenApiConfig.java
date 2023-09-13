package com.tum.in.cm.platformservice.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for OpenAPI and Swagger.
 * We define the security scheme as a Bearer Auth with JWT token.
 */
@Configuration
@SecurityScheme(
        name = "Bearer_Authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer",
        description = "A JWT token is required to access this API. JWT token can be obtained from the auth/request API."
)
public class OpenApiConfig {
}

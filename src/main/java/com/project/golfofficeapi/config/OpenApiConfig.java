package com.project.golfofficeapi.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String BEARER_AUTH = "bearerAuth";

    @Bean
    OpenAPI customOpenAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH))
                .info(new Info()
                        .title("Golf Office API")
                        .version("v1")
                        .description("RESTful API for golf course operations, including players, tee times, bookings," +
                                " rentals, payments, receipts, check-in tickets," +
                                " cash register closing, and rental damage reports.")
                        .termsOfService("https://github.com/luancolombo")
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/license/mit")
                        )
                )
                .components(new Components()
                        .addResponses("NoContent", new ApiResponse()
                                .description("No Content"))
                        .addResponses("BadRequest", errorResponse("Bad Request"))
                        .addResponses("Unauthorized", errorResponse("Unauthorized"))
                        .addResponses("Forbidden", errorResponse("Forbidden"))
                        .addResponses("NotFound", errorResponse("Not Found"))
                        .addResponses("Conflict", errorResponse("Conflict"))
                        .addResponses("InternalServerError", errorResponse("Internal Server Error"))
                        .addSecuritySchemes(BEARER_AUTH, new SecurityScheme()
                                .name(BEARER_AUTH)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Paste the JWT access token returned by /auth/login or /auth/refresh.")
                        )
                );
    }

    private Schema<?> exceptionResponseSchema() {
        return new Schema<>()
                .type("object")
                .addProperty("timestamp", new StringSchema().format("date-time"))
                .addProperty("message", new StringSchema())
                .addProperty("details", new StringSchema());
    }

    private ApiResponse errorResponse(String description) {
        return new ApiResponse()
                .description(description)
                .content(new Content()
                        .addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE, new MediaType()
                                .schema(exceptionResponseSchema())
                        )
                );
    }
}

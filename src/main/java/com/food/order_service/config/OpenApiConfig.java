package com.food.order_service.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI orderServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Order Service API")
                        .description("APIs for creating orders")
                        .version("v0.0.1")
                        .license(new License().name("MIT")))
                .externalDocs(new ExternalDocumentation().description("Project docs"));
    }
}


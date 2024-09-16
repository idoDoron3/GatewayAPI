package com.example.apigetawayservice.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("dal_service", r -> r.path("/api/articles/**")
                        .uri("http://localhost:8081")) // Forward to DAL service
                .route("parser_service", r -> r.path("/api/parser/**")
                        .uri("http://localhost:8082")) // Forward to Parser service
                .build();
    }
}

package com.eazybytes.gatewayserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

import static org.springframework.cloud.gateway.support.RouteMetadataUtils.CONNECT_TIMEOUT_ATTR;
import static org.springframework.cloud.gateway.support.RouteMetadataUtils.RESPONSE_TIMEOUT_ATTR;

@EnableDiscoveryClient
@SpringBootApplication
public class GatewayserverApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayserverApplication.class, args);
    }

    @Bean
    public RouteLocator eazyBankRouteConfig(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder.routes()
                .route(p -> p.path("/eazybank/accounts/**")
                        .filters(f -> f.rewritePath("/eazybank/accounts/(?<segment>.*)", "/${segment}")
                                .addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
                                .circuitBreaker(config -> config.setName("accountCircuitBreaker")
                                        .setFallbackUri("forward:/contactSupport"))
                        )
                        .metadata(RESPONSE_TIMEOUT_ATTR, 20000)
                        .metadata(CONNECT_TIMEOUT_ATTR, 1000)
                        .uri("lb://ACCOUNTS"))
                .route(p -> p.path("/eazybank/cards/**")
                        .filters(f -> f.rewritePath("/eazybank/cards/(?<segment>.*)", "/${segment}")
                                .addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
                                .circuitBreaker(config -> config.setName("cardCircuitBreaker")
                                        .setFallbackUri("forward:/contactSupport"))
                        )
                        .metadata(RESPONSE_TIMEOUT_ATTR, 20000)
                        .metadata(CONNECT_TIMEOUT_ATTR, 1000)
                        .uri("lb://CARDS"))
                .route(p -> p.path("/eazybank/loans/**")
                        .filters(f -> f.rewritePath("/eazybank/loans/(?<segment>.*)", "/${segment}")
                                .addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
                                .circuitBreaker(config -> config.setName("loanCircuitBreaker")
                                        .setFallbackUri("forward:/contactSupport"))
                        )
                        .metadata(RESPONSE_TIMEOUT_ATTR, 20000)
                        .metadata(CONNECT_TIMEOUT_ATTR, 1000)
                        .uri("lb://LOANS"))
                .build();
    }
}

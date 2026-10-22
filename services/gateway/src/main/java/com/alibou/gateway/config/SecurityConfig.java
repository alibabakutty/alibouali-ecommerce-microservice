package com.alibou.gateway.config;

import com.alibou.gateway.security.CustomAccessDeniedHandler;
import com.alibou.gateway.security.CustomAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    public SecurityConfig(CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
                          CustomAccessDeniedHandler customAccessDeniedHandler) {
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http
    ) {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                .authorizeExchange(exchange -> exchange

                        // =================================================
                        // AUTHENTICATION SERVICE
                        // =================================================

                        .pathMatchers("/api/v1/auth/**")
                        .permitAll()

                        // =================================================
                        // PRODUCT SERVICE
                        // =================================================

                        // USER and ADMIN can view products
                        .pathMatchers(
                                HttpMethod.GET,
                                "/api/v1/products/**"
                        )
                        .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")

                        // Only ADMIN can create products
                        .pathMatchers(
                                HttpMethod.POST,
                                "/api/v1/products/**"
                        )
                        .hasAuthority("ROLE_ADMIN")

                        // Only ADMIN can update products
                        .pathMatchers(
                                HttpMethod.PUT,
                                "/api/v1/products/**"
                        )
                        .hasAuthority("ROLE_ADMIN")

                        // Only ADMIN can delete products
                        .pathMatchers(
                                HttpMethod.DELETE,
                                "/api/v1/products/**"
                        )
                        .hasAuthority("ROLE_ADMIN")

                        // =================================================
                        // CUSTOMER SERVICE
                        // =================================================

                        // USER and ADMIN can view customers
                        .pathMatchers(
                                HttpMethod.GET,
                                "/api/v1/customers/**"
                        )
                        .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")

                        // Only ADMIN can create customers
                        .pathMatchers(
                                HttpMethod.POST,
                                "/api/v1/customers/**"
                        )
                        .hasAuthority("ROLE_ADMIN")

                        // Only ADMIN can update customers
                        .pathMatchers(
                                HttpMethod.PUT,
                                "/api/v1/customers/**"
                        )
                        .hasAuthority("ROLE_ADMIN")

                        // Only ADMIN can delete customers
                        .pathMatchers(
                                HttpMethod.DELETE,
                                "/api/v1/customers/**"
                        )
                        .hasAuthority("ROLE_ADMIN")

                        // =================================================
                        // ORDER SERVICE
                        // =================================================

                        // USER and ADMIN can view orders
                        .pathMatchers(
                                HttpMethod.GET,
                                "/api/v1/orders/**"
                        )
                        .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")

                        // USER and ADMIN can create orders
                        .pathMatchers(
                                HttpMethod.POST,
                                "/api/v1/orders/**"
                        )
                        .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")

                        // Only ADMIN can update orders
                        .pathMatchers(
                                HttpMethod.PUT,
                                "/api/v1/orders/**"
                        )
                        .hasAuthority("ROLE_ADMIN")

                        // Only ADMIN can delete orders
                        .pathMatchers(
                                HttpMethod.DELETE,
                                "/api/v1/orders/**"
                        )
                        .hasAuthority("ROLE_ADMIN")

                        // =================================================
                        // ORDER LINES
                        // =================================================

                        // USER and ADMIN can view order lines
                        .pathMatchers(
                                HttpMethod.GET,
                                "/api/v1/order-lines/**"
                        )
                        .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")

                        // USER and ADMIN can create order lines
                        .pathMatchers(
                                HttpMethod.POST,
                                "/api/v1/order-lines/**"
                        )
                        .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")

                        // Only ADMIN can update order lines
                        .pathMatchers(
                                HttpMethod.PUT,
                                "/api/v1/order-lines/**"
                        )
                        .hasAuthority("ROLE_ADMIN")

                        // Only ADMIN can delete order lines
                        .pathMatchers(
                                HttpMethod.DELETE,
                                "/api/v1/order-lines/**"
                        )
                        .hasAuthority("ROLE_ADMIN")

                        // =================================================
                        // PAYMENT SERVICE
                        // =================================================

                        // USER and ADMIN can view payments
                        .pathMatchers(
                                HttpMethod.GET,
                                "/api/v1/payments/**"
                        )
                        .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")

                        // USER and ADMIN can create payments
                        .pathMatchers(
                                HttpMethod.POST,
                                "/api/v1/payments/**"
                        )
                        .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")

                        // Only ADMIN can update payments
                        .pathMatchers(
                                HttpMethod.PUT,
                                "/api/v1/payments/**"
                        )
                        .hasAuthority("ROLE_ADMIN")

                        // Only ADMIN can delete payments
                        .pathMatchers(
                                HttpMethod.DELETE,
                                "/api/v1/payments/**"
                        )
                        .hasAuthority("ROLE_ADMIN")

                        // =================================================
                        // ALL OTHER ENDPOINTS
                        // =================================================

                        .anyExchange()
                        .authenticated()
                )

                .oauth2ResourceServer(oauth2 ->
                        oauth2
                                .authenticationEntryPoint(
                                        customAuthenticationEntryPoint
                                )
                                .accessDeniedHandler(
                                        customAccessDeniedHandler
                                )
                                .jwt(jwt ->
                                        jwt.jwtAuthenticationConverter(
                                                jwtAuthenticationConverter()
                                        )
                                )
                )

                .build();
    }

    @Bean
    public Converter<Jwt, Mono<AbstractAuthenticationToken>>
    jwtAuthenticationConverter() {

        return jwt -> {

            String role = jwt.getClaimAsString("role");

            if (role == null || role.isBlank()) {
                return Mono.just(
                        new JwtAuthenticationToken(
                                jwt,
                                List.of()
                        )
                );
            }

            return Mono.just(
                    new JwtAuthenticationToken(
                            jwt,
                            List.of(
                                    new SimpleGrantedAuthority(role)
                            )
                    )
            );
        };
    }
}
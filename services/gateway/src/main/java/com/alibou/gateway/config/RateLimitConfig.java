package com.alibou.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimitConfig {

    @Bean
    @Primary
    public KeyResolver userKeyResolver() {
        return exchange ->
                exchange.getPrincipal()
                        .map(principal -> principal.getName())
                        .defaultIfEmpty("anonymous");
    }

    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> {
            if (exchange.getRequest().getRemoteAddress() == null) {
                return Mono.just("unknown-ip");
            }

            if (exchange.getRequest().getRemoteAddress().getAddress() == null) {
                return Mono.just("unknown-ip");
            }

            return Mono.just(
                    exchange.getRequest()
                            .getRemoteAddress()
                            .getAddress()
                            .getHostAddress()
            );
        };
    }
}
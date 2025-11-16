package com.clusterat.live.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableReactiveMethodSecurity
class SecurityConfiguration {

    private final ApiKeyConfiguration apiKeyConfiguration;

    @Autowired
    public SecurityConfiguration(ApiKeyConfiguration apiKeyConfiguration) {
        this.apiKeyConfiguration = apiKeyConfiguration;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) throws Exception {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .securityContextRepository(apiKeyConfiguration)
                .authorizeExchange(auth -> auth
                        .pathMatchers("/v1/webhooks/brightdata/**").permitAll()
                        .pathMatchers("/actuator/**").permitAll()
                        .pathMatchers("/v1/**").authenticated()
                        .anyExchange().permitAll()
                )
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable);

        return http.build();
    }
}

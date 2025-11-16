package com.clusterat.live.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
class ApiKeyConfiguration implements ServerSecurityContextRepository {
    private static final String API_KEY_HEADER = "X-API-Key";

    @Value("${api.security.key}")
    private String validApiKey;

    @Value("${api.security.enabled:true}")
    private boolean securityEnabled;

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        return Mono.empty();
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        // Brightdata webhook has its own authentication filter (BrightdataApiKeyFilter)
        String path = exchange.getRequest().getPath().value();
        if (path.startsWith("/v1/webhooks/brightdata")) {
            log.debug("Skipping global API key validation for Brightdata webhook path: {}", path);
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    "brightdata-webhook",
                    null,
                    AuthorityUtils.createAuthorityList("ROLE_USER")
            );
            return Mono.just(new SecurityContextImpl(auth));
        }

        if (!securityEnabled) {
            log.warn("API Security is DISABLED - allowing all requests");
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    "api-user",
                    null,
                    AuthorityUtils.createAuthorityList("ROLE_USER")
            );
            return Mono.just(new SecurityContextImpl(auth));
        }

        String apiKey = exchange.getRequest().getHeaders().getFirst(API_KEY_HEADER);

        if (apiKey != null && apiKey.equals(validApiKey)) {
            log.debug("Valid API key provided");
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    "api-user",
                    null,
                    AuthorityUtils.createAuthorityList("ROLE_USER")
            );
            return Mono.just(new SecurityContextImpl(auth));
        }

        log.warn("Invalid or missing API key for request: {} {}",
                exchange.getRequest().getMethod(),
                exchange.getRequest().getPath());
        return Mono.empty();
    }
}

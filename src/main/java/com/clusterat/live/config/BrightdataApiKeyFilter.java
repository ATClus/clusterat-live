package com.clusterat.live.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class BrightdataApiKeyFilter implements WebFilter {
    private static final String API_KEY_HEADER = "X-API-Key";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BRIGHTDATA_WEBHOOK_PATH = "/v1/webhooks/brightdata";

    @Value("${brightdata.webhook.api-key:}")
    private String brightdataApiKey;

    @Value("${brightdata.webhook.enabled:true}")
    private boolean brightdataWebhookEnabled;

    @Override
    @SuppressWarnings("all")
    public Mono<Void> filter(@Nullable ServerWebExchange exchange, @Nullable WebFilterChain chain) {
        if (exchange == null || chain == null) {
            return Mono.empty();
        }

        String path = exchange.getRequest().getPath().value();

        if (path.startsWith(BRIGHTDATA_WEBHOOK_PATH)) {
            if (!brightdataWebhookEnabled) {
                log.warn("Attempt to access disabled Brightdata webhook");
                exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
                return exchange.getResponse().setComplete();
            }

            if (path.endsWith("/health")) {
                log.debug("Access to health check allowed without authentication");
                return chain.filter(exchange);
            }

            String apiKey = extractApiKey(exchange);

            log.info("=== API KEY VALIDATION DEBUG ===");
            log.info("Configured API Key: [{}] (length: {})", brightdataApiKey,
                brightdataApiKey != null ? brightdataApiKey.length() : 0);
            log.info("Received API Key: [{}] (length: {})", apiKey,
                apiKey != null ? apiKey.length() : 0);
            log.info("Keys are equal: {}", apiKey != null && apiKey.equals(brightdataApiKey));
            log.info("=== END DEBUG ===");

            if (brightdataApiKey == null || brightdataApiKey.trim().isEmpty()) {
                log.error("Critical error: brightdata.webhook.api-key is not configured in application.properties");
                return returnUnauthorized(exchange, "Webhook is not configured correctly on the server");
            }

            if (apiKey == null || apiKey.trim().isEmpty()) {
                log.warn("Request to Brightdata webhook without API Key. Path: {}", path);
                return returnUnauthorized(exchange, "API Key not provided");
            }

            if (!apiKey.equals(brightdataApiKey)) {
                log.warn("Request to Brightdata webhook with invalid API Key. Path: {} | Expected: {} | Received: {}",
                    path, brightdataApiKey, apiKey);
                return returnUnauthorized(exchange, "Invalid API Key");
            }

            log.debug("Request to Brightdata webhook authorized. Path: {}", path);
        }

        return chain.filter(exchange);
    }

    private String extractApiKey(ServerWebExchange exchange) {
        String apiKey = exchange.getRequest().getHeaders().getFirst(API_KEY_HEADER);

        if (apiKey != null && !apiKey.trim().isEmpty()) {
            log.debug("API Key found in X-API-Key header");
            return apiKey.trim();
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(AUTHORIZATION_HEADER);

        if (authHeader != null && !authHeader.trim().isEmpty()) {
            log.info("Authorization header found: [{}] (length: {})", authHeader, authHeader.length());

            if (authHeader.startsWith("X-API-Key:")) {
                apiKey = authHeader.substring("X-API-Key:".length()).trim();
                log.info("API Key extracted from Authorization header (X-API-Key: format): [{}]", apiKey);
                return apiKey;
            }

            if (authHeader.startsWith("Bearer ")) {
                apiKey = authHeader.substring("Bearer ".length()).trim();
                log.info("API Key extracted from Authorization header (Bearer format): [{}]", apiKey);
                return apiKey;
            }

            log.info("Using direct value from Authorization header: [{}]", authHeader.trim());
            return authHeader.trim();
        }

        log.debug("No API Key found in headers");
        return null;
    }

    private Mono<Void> returnUnauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");

        String errorBody = String.format(
                "{\"success\": false, \"message\": \"%s\"}",
                message
        );

        return exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse()
                        .bufferFactory()
                        .wrap(errorBody.getBytes())));
    }
}

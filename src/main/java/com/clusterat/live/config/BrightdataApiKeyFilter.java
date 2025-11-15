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

/**
 * WebFilter para validar API Key do Brightdata em requisições ao webhook
 */
@Slf4j
@Component
public class BrightdataApiKeyFilter implements WebFilter {
    private static final String API_KEY_HEADER = "X-API-Key";
    private static final String BRIGHTDATA_WEBHOOK_PATH = "/api/v1/webhooks/brightdata";

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

        // Aplicar validação apenas para endpoints do Brightdata
        if (path.startsWith(BRIGHTDATA_WEBHOOK_PATH)) {
            if (!brightdataWebhookEnabled) {
                log.warn("Tentativa de acesso ao webhook Brightdata desabilitado");
                exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
                return exchange.getResponse().setComplete();
            }

            // Health check não requer API Key
            if (path.endsWith("/health")) {
                log.debug("Acesso ao health check permitido sem autenticação");
                return chain.filter(exchange);
            }

            // Validar API Key para outros endpoints
            String apiKey = exchange.getRequest().getHeaders().getFirst(API_KEY_HEADER);

            log.debug("Validando API Key. Configurada: '{}', Enviada: '{}'", brightdataApiKey, apiKey);

            // Verificar se a API Key está configurada
            if (brightdataApiKey == null || brightdataApiKey.trim().isEmpty()) {
                log.error("Erro crítico: brightdata.webhook.api-key não está configurada em application.properties");
                return returnUnauthorized(exchange, "Webhook não está configurado corretamente no servidor");
            }

            if (apiKey == null || apiKey.trim().isEmpty()) {
                log.warn("Requisição ao webhook Brightdata sem API Key. Path: {}", path);
                return returnUnauthorized(exchange, "API Key não fornecida");
            }

            if (!apiKey.equals(brightdataApiKey)) {
                log.warn("Requisição ao webhook Brightdata com API Key inválida. Path: {} | Esperada: {} | Recebida: {}",
                    path, brightdataApiKey, apiKey);
                return returnUnauthorized(exchange, "API Key inválida");
            }

            log.debug("Requisição ao webhook Brightdata autorizada. Path: {}", path);
        }

        return chain.filter(exchange);
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



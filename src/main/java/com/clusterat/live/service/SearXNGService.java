package com.clusterat.live.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
public class SearXNGService {
    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String searXNGUrl;
    private final long rateLimitWindowMs;
    private final int maxRequestsPerWindow;

    // Rate limiting
    private final ConcurrentHashMap<String, RateLimitInfo> rateLimitMap = new ConcurrentHashMap<>();

    private static class RateLimitInfo {
        AtomicInteger requestCount = new AtomicInteger(0);
        AtomicLong windowStartTime = new AtomicLong(System.currentTimeMillis());
    }

    @Autowired
    public SearXNGService(RestClient restClient, ObjectMapper objectMapper,
                         @Value("${searxng.url}") String searXNGUrl,
                         @Value("${searxng.timeout.seconds:10}") int searchTimeoutSeconds,
                         @Value("${searxng.retry.max-attempts:3}") int maxRetries,
                         @Value("${searxng.rate-limit.window-ms:60000}") long rateLimitWindowMs,
                         @Value("${searxng.rate-limit.max-requests:100}") int maxRequestsPerWindow) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
        this.searXNGUrl = searXNGUrl;
        this.rateLimitWindowMs = rateLimitWindowMs;
        this.maxRequestsPerWindow = maxRequestsPerWindow;
    }

    /**
     * Realiza uma busca no SearXNG com tratamento robusto de erros.
     * @param query Termo de busca
     * @return Resposta JSON da busca ou JSON vazio em caso de erro
     */
    @Cacheable(value = "searxng_searches", key = "#query", unless = "#result == null || #result.isEmpty()")
    public String search(String query) {
        return search(query, 1, null, null);
    }

    /**
     * Realiza uma busca com parâmetros avançados.
     * @param query Termo de busca
     * @param page Página de resultados (padrão: 1)
     * @param category Categoria de busca (opcional)
     * @param language Idioma dos resultados (opcional)
     * @return Resposta JSON da busca
     */
    public String search(String query, Integer page, String category, String language) {
        // Validação de entrada
        if (query == null || query.trim().isEmpty()) {
            log.warn("Search query is empty");
            return createErrorResponse("Search query cannot be empty");
        }

        if (query.length() > 1000) {
            log.warn("Search query exceeds maximum length: {}", query.length());
            return createErrorResponse("Search query exceeds maximum length of 1000 characters");
        }

        if (!checkRateLimit(query)) {
            log.warn("Rate limit exceeded for query: {}", query);
            return createErrorResponse("Rate limit exceeded. Please try again later");
        }

        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = buildSearchUrl(encodedQuery, page, category, language);

            log.debug("Searching SearXNG with URL: {}", url);

            ResponseEntity<String> response = restClient.get()
                    .uri(url)
                    .retrieve()
                    .toEntity(String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                String body = response.getBody();
                if (body != null && !body.isEmpty()) {
                    log.info("Search successful for query: {}", query);
                    return body;
                } else {
                    log.warn("Empty response body from SearXNG for query: {}", query);
                    return createErrorResponse("Empty response from SearXNG");
                }
            } else {
                log.error("Unexpected status code from SearXNG: {}", response.getStatusCode());
                return createErrorResponse("Unexpected status: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            log.error("Client error during search for query '{}': {} - {}",
                    query, e.getStatusCode(), e.getMessage(), e);
            return createErrorResponse("Client error: " + e.getStatusCode() + " - " + e.getMessage());
        } catch (HttpServerErrorException e) {
            log.error("Server error during search for query '{}': {} - {}",
                    query, e.getStatusCode(), e.getMessage(), e);
            return createErrorResponse("Server error: " + e.getStatusCode() + " - " + e.getMessage());
        } catch (RestClientException e) {
            log.error("REST client error during search for query '{}': {}", query, e.getMessage(), e);
            return createErrorResponse("REST client error: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during search for query '{}': {}", query, e.getMessage(), e);
            return createErrorResponse("Unexpected error: " + e.getMessage());
        }
    }

    /**
     * Realiza uma busca avançada com múltiplos parâmetros.
     * @param query Termo de busca
     * @param params Parâmetros adicionais
     * @return Resposta JSON da busca
     */
    public String advancedSearch(String query, Map<String, String> params) {
        if (query == null || query.trim().isEmpty()) {
            return createErrorResponse("Search query cannot be empty");
        }

        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            StringBuilder urlBuilder = new StringBuilder(searXNGUrl)
                    .append("/search?q=").append(encodedQuery)
                    .append("&format=json");

            // Adicionar parâmetros opcionais
            if (params != null) {
                params.forEach((key, value) ->
                    urlBuilder.append("&").append(key).append("=")
                            .append(URLEncoder.encode(value, StandardCharsets.UTF_8))
                );
            }

            String url = urlBuilder.toString();
            log.debug("Advanced search URL: {}", url);

            String response = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(String.class);

            return response != null ? response : createErrorResponse("Empty response");
        } catch (Exception e) {
            log.error("Error during advanced search for query '{}': {}", query, e.getMessage(), e);
            return createErrorResponse("Advanced search error: " + e.getMessage());
        }
    }

    /**
     * Limpa o cache de buscas.
     */
    @CacheEvict(value = "searxng_searches", allEntries = true)
    public void clearCache() {
        log.info("Clearing SearXNG search cache");
    }

    /**
     * Limpa o cache para uma query específica.
     */
    @CacheEvict(value = "searxng_searches", key = "#query")
    public void clearCacheForQuery(String query) {
        log.info("Clearing SearXNG search cache for query: {}", query);
    }

    /**
     * Verifica a saúde do serviço SearXNG.
     * @return true se o serviço está disponível
     */
    public boolean healthCheck() {
        try {
            log.debug("Checking SearXNG health");
            ResponseEntity<String> response = restClient.get()
                    .uri(searXNGUrl + "/status")
                    .retrieve()
                    .toEntity(String.class);

            boolean isHealthy = response.getStatusCode() == HttpStatus.OK;
            log.info("SearXNG health check: {}", isHealthy ? "HEALTHY" : "UNHEALTHY");
            return isHealthy;
        } catch (Exception e) {
            log.error("SearXNG health check failed: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Obtém informações sobre o servidor SearXNG.
     * @return JSON com informações do servidor
     */
    public String getServerInfo() {
        try {
            log.debug("Getting SearXNG server info");
            String response = restClient.get()
                    .uri(searXNGUrl + "/config")
                    .retrieve()
                    .body(String.class);
            return response != null ? response : createErrorResponse("No server info available");
        } catch (Exception e) {
            log.error("Error getting SearXNG server info: {}", e.getMessage(), e);
            return createErrorResponse("Failed to get server info: " + e.getMessage());
        }
    }

    private String buildSearchUrl(String encodedQuery, Integer page, String category, String language) {
        StringBuilder urlBuilder = new StringBuilder(searXNGUrl)
                .append("/search?q=").append(encodedQuery)
                .append("&format=json");

        if (page != null && page > 0) {
            urlBuilder.append("&pageno=").append(page);
        }

        if (category != null && !category.isEmpty()) {
            urlBuilder.append("&category=").append(URLEncoder.encode(category, StandardCharsets.UTF_8));
        }

        if (language != null && !language.isEmpty()) {
            urlBuilder.append("&lang=").append(URLEncoder.encode(language, StandardCharsets.UTF_8));
        }

        return urlBuilder.toString();
    }

    private boolean checkRateLimit(String clientId) {
        long currentTime = System.currentTimeMillis();
        RateLimitInfo info = rateLimitMap.computeIfAbsent(clientId, k -> new RateLimitInfo());

        long windowAge = currentTime - info.windowStartTime.get();
        if (windowAge >= rateLimitWindowMs) {
            // Nova janela
            info.requestCount.set(1);
            info.windowStartTime.set(currentTime);
            return true;
        }

        int currentCount = info.requestCount.incrementAndGet();
        return currentCount <= maxRequestsPerWindow;
    }

    private String createErrorResponse(String message) {
        try {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", message);
            errorResponse.put("timestamp", System.currentTimeMillis());
            errorResponse.put("results", new Object[0]);
            return objectMapper.writeValueAsString(errorResponse);
        } catch (Exception e) {
            log.error("Error creating error response: {}", e.getMessage(), e);
            return "{\"error\":\"" + message + "\",\"results\":[]}";
        }
    }
}

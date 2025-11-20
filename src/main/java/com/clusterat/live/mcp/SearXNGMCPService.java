package com.clusterat.live.mcp;

import com.clusterat.live.service.SearXNGService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * MCP Service for SearXNG search operations.
 * Provides AI-accessible tools for web search functionality.
 */
@Slf4j
@Service
public class SearXNGMCPService {

    private final SearXNGService searXNGService;
    private final ObjectMapper objectMapper;

    public SearXNGMCPService(SearXNGService searXNGService, ObjectMapper objectMapper) {
        this.searXNGService = searXNGService;
        this.objectMapper = objectMapper;
    }

    /**
     * Performs a simple web search using SearXNG.
     *
     * @param query The search query string (required, max 1000 characters)
     * @return ResponseEntity with search results in JSON format
     */
    @Tool(description = "Search the web using SearXNG. Returns search results including titles, URLs, and content snippets. Query must be between 1 and 1000 characters.")
    public ResponseEntity<Map<String, Object>> searchWeb(String query) {
        try {
            log.info("MCP Tool: searchWeb called with query: {}", query);

            if (query == null || query.trim().isEmpty()) {
                return createErrorResponse("Query cannot be empty", HttpStatus.BAD_REQUEST);
            }

            String resultJson = searXNGService.search(query);
            Map<String, Object> result = parseJsonResponse(resultJson);

            log.info("MCP Tool: searchWeb completed successfully for query: {}", query);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("Error in MCP searchWeb for query '{}': {}", query, e.getMessage(), e);
            return createErrorResponse("Search failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Performs an advanced web search with pagination and filtering options.
     *
     * @param query The search query string (required)
     * @param page Page number for pagination (optional, default: 1)
     * @param category Search category filter (optional, e.g., "news", "images", "social media")
     * @param language Language code for results (optional, e.g., "en-US", "pt-BR")
     * @return ResponseEntity with filtered search results
     */
    @Tool(description = "Perform advanced web search with filters. Supports pagination (page), category filtering (news, images, social media, etc.), and language selection (en-US, pt-BR, etc.).")
    public ResponseEntity<Map<String, Object>> advancedSearch(
            String query,
            Integer page,
            String category,
            String language) {
        try {
            log.info("MCP Tool: advancedSearch called with query: {}, page: {}, category: {}, language: {}",
                    query, page, category, language);

            if (query == null || query.trim().isEmpty()) {
                return createErrorResponse("Query cannot be empty", HttpStatus.BAD_REQUEST);
            }

            String resultJson = searXNGService.search(
                    query,
                    page != null ? page : 1,
                    category,
                    language
            );

            Map<String, Object> result = parseJsonResponse(resultJson);

            log.info("MCP Tool: advancedSearch completed successfully");
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("Error in MCP advancedSearch: {}", e.getMessage(), e);
            return createErrorResponse("Advanced search failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Checks the health status of the SearXNG service.
     *
     * @return ResponseEntity with health status information
     */
    @Tool(description = "Check if the SearXNG search service is available and responding. Returns health status and timestamp.")
    public ResponseEntity<Map<String, Object>> checkSearchHealth() {
        try {
            log.info("MCP Tool: checkSearchHealth called");

            boolean healthy = searXNGService.healthCheck();

            Map<String, Object> response = new HashMap<>();
            response.put("healthy", healthy);
            response.put("status", healthy ? "HEALTHY" : "UNHEALTHY");
            response.put("service", "SearXNG");
            response.put("timestamp", System.currentTimeMillis());

            log.info("MCP Tool: checkSearchHealth completed - status: {}", healthy ? "HEALTHY" : "UNHEALTHY");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error in MCP checkSearchHealth: {}", e.getMessage(), e);
            return createErrorResponse("Health check failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves information about the SearXNG server configuration.
     *
     * @return ResponseEntity with server configuration details
     */
    @Tool(description = "Get SearXNG server information including available categories, search engines, and configuration. Useful for understanding search capabilities.")
    public ResponseEntity<Map<String, Object>> getServerInfo() {
        try {
            log.info("MCP Tool: getServerInfo called");

            String infoJson = searXNGService.getServerInfo();
            Map<String, Object> result = parseJsonResponse(infoJson);

            log.info("MCP Tool: getServerInfo completed successfully");
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("Error in MCP getServerInfo: {}", e.getMessage(), e);
            return createErrorResponse("Failed to get server info: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Clears the search cache, forcing fresh results on next search.
     *
     * @return ResponseEntity with cache clearing status
     */
    @Tool(description = "Clear the search results cache. Use this to force fresh search results instead of cached ones.")
    public ResponseEntity<Map<String, Object>> clearSearchCache() {
        try {
            log.info("MCP Tool: clearSearchCache called");

            searXNGService.clearCache();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Search cache cleared successfully");
            response.put("timestamp", System.currentTimeMillis());

            log.info("MCP Tool: clearSearchCache completed successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error in MCP clearSearchCache: {}", e.getMessage(), e);
            return createErrorResponse("Failed to clear cache: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Parses JSON string response into Map.
     *
     * @param jsonString JSON string to parse
     * @return Map representation of JSON
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> parseJsonResponse(String jsonString) {
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonString);
            return objectMapper.convertValue(jsonNode, Map.class);
        } catch (Exception e) {
            log.warn("Failed to parse JSON response: {}", e.getMessage());
            Map<String, Object> fallback = new HashMap<>();
            fallback.put("raw_response", jsonString);
            fallback.put("parse_error", e.getMessage());
            return fallback;
        }
    }

    /**
     * Creates a standardized error response.
     *
     * @param message Error message
     * @param status HTTP status code
     * @return ResponseEntity with error details
     */
    private ResponseEntity<Map<String, Object>> createErrorResponse(String message, HttpStatus status) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", message);
        error.put("status", status.value());
        error.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.status(status).body(error);
    }
}


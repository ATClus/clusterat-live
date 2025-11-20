package com.clusterat.live.controller;

import com.clusterat.live.dto.SearXNGSearchRequestDTO;
import com.clusterat.live.service.SearXNGService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller to manage search operations via SearXNG
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/search")
@Tag(name = "Search", description = "Search operations through SearXNG")
public class SearXNGController {

    private final SearXNGService searXNGService;

    @Autowired
    public SearXNGController(SearXNGService searXNGService) {
        this.searXNGService = searXNGService;
    }

    /**
     * Performs a simple search
     */
    @GetMapping("/simple")
    @Operation(summary = "Simple search", description = "Performs a simple search with a search term")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search performed successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Invalid parameters"),
            @ApiResponse(responseCode = "503", description = "SearXNG service unavailable")
    })
    public ResponseEntity<String> simpleSearch(
            @RequestParam(name = "q")
            @Parameter(description = "Search term", required = true)
            String query) {

        log.info("Received simple search request for query: {}", query);

        try {
            String result = searXNGService.search(query);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(result);
        } catch (Exception e) {
            log.error("Error during simple search", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    /**
     * Performs an advanced search with multiple parameters
     */
    @PostMapping("/advanced")
    @Operation(summary = "Advanced search", description = "Performs a search with advanced parameters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search performed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters"),
            @ApiResponse(responseCode = "503", description = "SearXNG service unavailable")
    })
    public ResponseEntity<String> advancedSearch(
            @RequestBody SearXNGSearchRequestDTO request) {

        log.info("Received advanced search request for query: {}", request.getQuery());

        try {
            String result = searXNGService.search(
                    request.getQuery(),
                    request.getPage() != null ? request.getPage() : 1,
                    request.getCategory(),
                    request.getLanguage()
            );
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(result);
        } catch (Exception e) {
            log.error("Error during advanced search", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    /**
     * Performs a search with custom parameters
     */
    @GetMapping("/custom")
    @Operation(summary = "Custom search", description = "Performs a search with additional custom parameters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search performed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters"),
            @ApiResponse(responseCode = "503", description = "SearXNG service unavailable")
    })
    public ResponseEntity<String> customSearch(
            @RequestParam(name = "q")
            @Parameter(description = "Search term", required = true)
            String query,

            @RequestParam(name = "page", required = false, defaultValue = "1")
            @Parameter(description = "Page number")
            Integer page,

            @RequestParam(name = "category", required = false)
            @Parameter(description = "Search category")
            String category,

            @RequestParam(name = "lang", required = false)
            @Parameter(description = "Language of results")
            String language) {

        log.info("Received custom search request for query: {} with page: {}, category: {}, language: {}",
                query, page, category, language);

        try {
            String result = searXNGService.search(query, page, category, language);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(result);
        } catch (Exception e) {
            log.error("Error during custom search", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    /**
     * Clears the search cache
     */
    @DeleteMapping("/cache")
    @Operation(summary = "Clear cache", description = "Clears the cache of all searches")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cache cleared successfully"),
            @ApiResponse(responseCode = "500", description = "Error clearing cache")
    })
    public ResponseEntity<Map<String, String>> clearCache() {
        log.info("Clearing search cache");

        try {
            searXNGService.clearCache();
            Map<String, String> response = new HashMap<>();
            response.put("message", "Cache cleared successfully");
            response.put("timestamp", String.valueOf(System.currentTimeMillis()));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error clearing cache", e);
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Clears the cache for a specific query
     */
    @DeleteMapping("/cache/{query}")
    @Operation(summary = "Clear query cache", description = "Clears the cache for a specific query")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Query cache cleared successfully"),
            @ApiResponse(responseCode = "500", description = "Error clearing cache")
    })
    public ResponseEntity<Map<String, String>> clearCacheForQuery(
            @PathVariable(name = "query")
            @Parameter(description = "Search term")
            String query) {

        log.info("Clearing search cache for query: {}", query);

        try {
            searXNGService.clearCacheForQuery(query);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Cache cleared successfully for query: " + query);
            response.put("query", query);
            response.put("timestamp", String.valueOf(System.currentTimeMillis()));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error clearing cache for query", e);
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Checks the health of the SearXNG service
     */
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Checks if the SearXNG service is available")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service is healthy"),
            @ApiResponse(responseCode = "503", description = "Service unavailable")
    })
    public ResponseEntity<Map<String, Object>> healthCheck() {
        log.info("Performing SearXNG health check");

        try {
            boolean healthy = searXNGService.healthCheck();

            Map<String, Object> response = new HashMap<>();
            response.put("status", healthy ? "HEALTHY" : "UNHEALTHY");
            response.put("healthy", healthy);
            response.put("timestamp", System.currentTimeMillis());

            return healthy ?
                    ResponseEntity.ok(response) :
                    ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
        } catch (Exception e) {
            log.error("Error during health check", e);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "UNHEALTHY");
            response.put("healthy", false);
            response.put("error", e.getMessage());
            response.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
        }
    }

    /**
     * Gets information about the SearXNG server
     */
    @GetMapping("/info")
    @Operation(summary = "Server information", description = "Gets information about the SearXNG server")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Information retrieved successfully"),
            @ApiResponse(responseCode = "503", description = "Service unavailable")
    })
    public ResponseEntity<String> getServerInfo() {
        log.info("Getting SearXNG server info");

        try {
            String info = searXNGService.getServerInfo();
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(info);
        } catch (Exception e) {
            log.error("Error getting server info", e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}

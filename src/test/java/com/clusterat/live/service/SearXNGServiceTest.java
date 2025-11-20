package com.clusterat.live.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@DisplayName("SearXNG Service Tests")
class SearXNGServiceTest {

    private SearXNGService searXNGService;

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        searXNGService = new SearXNGService(
                restClient,
                objectMapper,
                "http://searxng:8080",
                10,
                3,
                60000,
                100
        );
    }

    @Test
    @DisplayName("Should perform a simple search successfully")
    void testSimpleSearchSuccess() {
        // Arrange
        String query = "java spring boot";
        String mockResponse = "{\"results\":[],\"number_of_results\":0}";

        when(restClient.get()).thenReturn((RestClient.RequestHeadersUriSpec) requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn((RestClient.RequestHeadersUriSpec) requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(String.class)).thenReturn(ResponseEntity.ok(mockResponse));

        // Act
        String result = searXNGService.search(query);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("results"));
    }

    @Test
    @DisplayName("Should return error for empty query")
    void testSearchWithEmptyQuery() {
        // Arrange
        String query = "";

        // Act
        String result = searXNGService.search(query);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("error"));
        assertTrue(result.contains("empty"));
    }

    @Test
    @DisplayName("Should return error for query exceeding max length")
    void testSearchWithQueryExceedingMaxLength() {
        // Arrange
        String query = "a".repeat(1001);

        // Act
        String result = searXNGService.search(query);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("error"));
        assertTrue(result.contains("exceeds maximum length"));
    }

    @Test
    @DisplayName("Should perform health check successfully")
    void testHealthCheckSuccess() {
        // Arrange
        when(restClient.get()).thenReturn((RestClient.RequestHeadersUriSpec) requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn((RestClient.RequestHeadersUriSpec) requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(String.class)).thenReturn(ResponseEntity.ok("{}"));

        // Act
        boolean result = searXNGService.healthCheck();

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Should return false for health check failure")
    void testHealthCheckFailure() {
        // Arrange
        when(restClient.get()).thenReturn((RestClient.RequestHeadersUriSpec) requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn((RestClient.RequestHeadersUriSpec) requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenThrow(new RuntimeException("Connection failed"));

        // Act
        boolean result = searXNGService.healthCheck();

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Should clear cache successfully")
    void testClearCache() {
        // Act & Assert - should not throw exception
        assertDoesNotThrow(() -> searXNGService.clearCache());
    }

    @Test
    @DisplayName("Should perform advanced search with parameters")
    void testAdvancedSearchWithParameters() {
        // Arrange
        String query = "java";
        Integer page = 2;
        String category = "social media";
        String language = "pt-BR";
        String mockResponse = "{\"results\":[],\"number_of_results\":0}";

        when(restClient.get()).thenReturn((RestClient.RequestHeadersUriSpec) requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn((RestClient.RequestHeadersUriSpec) requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(String.class)).thenReturn(ResponseEntity.ok(mockResponse));

        // Act
        String result = searXNGService.search(query, page, category, language);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("results"));
    }
}


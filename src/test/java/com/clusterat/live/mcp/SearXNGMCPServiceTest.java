package com.clusterat.live.mcp;

import com.clusterat.live.service.SearXNGService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("SearXNG MCP Service Tests")
class SearXNGMCPServiceTest {

    private SearXNGMCPService mcpService;
    private SearXNGService searXNGService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        searXNGService = mock(SearXNGService.class);
        objectMapper = new ObjectMapper();
        mcpService = new SearXNGMCPService(searXNGService, objectMapper);
    }

    @Test
    @DisplayName("searchWeb should return search results successfully")
    void testSearchWebSuccess() {
        // Arrange
        String query = "java spring boot";
        String mockResponse = "{\"results\":[{\"title\":\"Test\",\"url\":\"http://test.com\"}],\"number_of_results\":1}";
        when(searXNGService.search(query)).thenReturn(mockResponse);

        // Act
        ResponseEntity<Map<String, Object>> response = mcpService.searchWeb(query);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("results"));
        verify(searXNGService).search(query);
    }

    @Test
    @DisplayName("searchWeb should return error for empty query")
    void testSearchWebEmptyQuery() {
        // Act
        ResponseEntity<Map<String, Object>> response = mcpService.searchWeb("");

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Query cannot be empty", response.getBody().get("error"));
        verify(searXNGService, never()).search(anyString());
    }

    @Test
    @DisplayName("searchWeb should return error for null query")
    void testSearchWebNullQuery() {
        // Act
        ResponseEntity<Map<String, Object>> response = mcpService.searchWeb(null);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("error"));
        verify(searXNGService, never()).search(anyString());
    }

    @Test
    @DisplayName("searchWeb should handle service exceptions gracefully")
    void testSearchWebServiceException() {
        // Arrange
        String query = "test";
        when(searXNGService.search(query)).thenThrow(new RuntimeException("Service error"));

        // Act
        ResponseEntity<Map<String, Object>> response = mcpService.searchWeb(query);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("error"));
    }

    @Test
    @DisplayName("advancedSearch should return results with all parameters")
    void testAdvancedSearchSuccess() {
        // Arrange
        String query = "java";
        Integer page = 2;
        String category = "news";
        String language = "pt-BR";
        String mockResponse = "{\"results\":[],\"number_of_results\":0}";

        when(searXNGService.search(query, page, category, language)).thenReturn(mockResponse);

        // Act
        ResponseEntity<Map<String, Object>> response = mcpService.advancedSearch(query, page, category, language);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(searXNGService).search(query, page, category, language);
    }

    @Test
    @DisplayName("advancedSearch should use default page when null")
    void testAdvancedSearchDefaultPage() {
        // Arrange
        String query = "test";
        String mockResponse = "{\"results\":[]}";

        when(searXNGService.search(eq(query), eq(1), isNull(), isNull())).thenReturn(mockResponse);

        // Act
        ResponseEntity<Map<String, Object>> response = mcpService.advancedSearch(query, null, null, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(searXNGService).search(query, 1, null, null);
    }

    @Test
    @DisplayName("advancedSearch should return error for empty query")
    void testAdvancedSearchEmptyQuery() {
        // Act
        ResponseEntity<Map<String, Object>> response = mcpService.advancedSearch("", 1, null, null);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("error"));
    }

    @Test
    @DisplayName("checkSearchHealth should return healthy status")
    void testCheckSearchHealthHealthy() {
        // Arrange
        when(searXNGService.healthCheck()).thenReturn(true);

        // Act
        ResponseEntity<Map<String, Object>> response = mcpService.checkSearchHealth();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue((Boolean) response.getBody().get("healthy"));
        assertEquals("HEALTHY", response.getBody().get("status"));
        assertEquals("SearXNG", response.getBody().get("service"));
        verify(searXNGService).healthCheck();
    }

    @Test
    @DisplayName("checkSearchHealth should return unhealthy status")
    void testCheckSearchHealthUnhealthy() {
        // Arrange
        when(searXNGService.healthCheck()).thenReturn(false);

        // Act
        ResponseEntity<Map<String, Object>> response = mcpService.checkSearchHealth();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse((Boolean) response.getBody().get("healthy"));
        assertEquals("UNHEALTHY", response.getBody().get("status"));
    }

    @Test
    @DisplayName("checkSearchHealth should handle exceptions")
    void testCheckSearchHealthException() {
        // Arrange
        when(searXNGService.healthCheck()).thenThrow(new RuntimeException("Connection error"));

        // Act
        ResponseEntity<Map<String, Object>> response = mcpService.checkSearchHealth();

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("error"));
    }

    @Test
    @DisplayName("getServerInfo should return server information")
    void testGetServerInfoSuccess() {
        // Arrange
        String mockInfo = "{\"version\":\"1.0\",\"engines\":[\"google\",\"bing\"]}";
        when(searXNGService.getServerInfo()).thenReturn(mockInfo);

        // Act
        ResponseEntity<Map<String, Object>> response = mcpService.getServerInfo();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(searXNGService).getServerInfo();
    }

    @Test
    @DisplayName("getServerInfo should handle exceptions")
    void testGetServerInfoException() {
        // Arrange
        when(searXNGService.getServerInfo()).thenThrow(new RuntimeException("Service unavailable"));

        // Act
        ResponseEntity<Map<String, Object>> response = mcpService.getServerInfo();

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("error"));
    }

    @Test
    @DisplayName("clearSearchCache should clear cache successfully")
    void testClearSearchCacheSuccess() {
        // Arrange
        doNothing().when(searXNGService).clearCache();

        // Act
        ResponseEntity<Map<String, Object>> response = mcpService.clearSearchCache();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue((Boolean) response.getBody().get("success"));
        assertTrue(response.getBody().containsKey("message"));
        verify(searXNGService).clearCache();
    }

    @Test
    @DisplayName("clearSearchCache should handle exceptions")
    void testClearSearchCacheException() {
        // Arrange
        doThrow(new RuntimeException("Cache error")).when(searXNGService).clearCache();

        // Act
        ResponseEntity<Map<String, Object>> response = mcpService.clearSearchCache();

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("error"));
    }

    @Test
    @DisplayName("should handle malformed JSON gracefully")
    void testMalformedJsonHandling() {
        // Arrange
        String query = "test";
        String malformedJson = "not a valid json";
        when(searXNGService.search(query)).thenReturn(malformedJson);

        // Act
        ResponseEntity<Map<String, Object>> response = mcpService.searchWeb(query);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("raw_response") || response.getBody().containsKey("parse_error"));
    }
}


package com.clusterat.live.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * DTO for SearXNG search requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearXNGSearchRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Search term (required)
     */
    private String query;

    /**
     * Results page (default: 1)
     */
    @Builder.Default
    private Integer page = 1;

    /**
     * Search category (optional)
     * Examples: general, images, news, social media, etc.
     */
    private String category;

    /**
     * Language of results (optional)
     * Examples: pt-BR, en-US, etc.
     */
    private String language;

    /**
     * Number of results per page (default: 10, max: 100)
     */
    @Builder.Default
    private Integer count = 10;

    /**
     * Additional custom parameters
     */
    private Map<String, String> customParameters;

    /**
     * Use cache (default: true)
     */
    @Builder.Default
    private Boolean useCache = true;
}

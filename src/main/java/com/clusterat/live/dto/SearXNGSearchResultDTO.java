package com.clusterat.live.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for SearXNG search results
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearXNGSearchResultDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * List of found results
     */
    private List<SearchResultItemDTO> results;

    /**
     * Pagination information
     */
    private PaginationInfoDTO pagination;

    /**
     * Error message (if any)
     */
    private String error;

    /**
     * Search timestamp
     */
    private Long timestamp;

    /**
     * Total results found
     */
    @JsonProperty("number_of_results")
    private Integer numberOfResults;

    /**
     * Total search time in milliseconds
     */
    @JsonProperty("search_duration_ms")
    private Long searchDurationMs;

    // ==================== Nested DTOs ====================

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SearchResultItemDTO implements Serializable {
        private String title;
        private String url;
        private String content;
        private String engine;

        @JsonProperty("parsed_url")
        private String parsedUrl;

        @JsonProperty("is_default")
        private Boolean isDefault;

        @JsonProperty("img_src")
        private String imgSrc;

        private String position;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PaginationInfoDTO implements Serializable {
        @JsonProperty("current_page")
        private Integer currentPage;

        @JsonProperty("total_pages")
        private Integer totalPages;

        @JsonProperty("results_per_page")
        private Integer resultsPerPage;

        @JsonProperty("has_next")
        private Boolean hasNext;

        @JsonProperty("has_previous")
        private Boolean hasPrevious;
    }
}

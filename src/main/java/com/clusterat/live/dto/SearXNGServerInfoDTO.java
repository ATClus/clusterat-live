package com.clusterat.live.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * DTO for SearXNG server information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearXNGServerInfoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Server status
     */
    private String status;

    /**
     * SearXNG version
     */
    private String version;

    /**
     * Available categories
     */
    private Map<String, Object> categories;

    /**
     * Available engines
     */
    private Map<String, Object> engines;

    /**
     * General configurations
     */
    private Map<String, Object> config;

    /**
     * Verification timestamp
     */
    private Long timestamp;

    /**
     * If the server is healthy
     */
    private Boolean healthy;
}

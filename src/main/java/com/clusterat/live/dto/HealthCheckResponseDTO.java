package com.clusterat.live.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthCheckResponseDTO {
    @JsonProperty("status")
    private String status;

    @JsonProperty("version")
    private String version;
}


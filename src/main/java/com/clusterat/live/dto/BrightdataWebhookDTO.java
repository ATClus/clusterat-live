package com.clusterat.live.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrightdataWebhookDTO {
    // Brightdata pode enviar dados em diversos formatos
    // Este DTO aceita qualquer estrutura JSON usando @JsonAnySetter
    @Builder.Default
    private Map<String, Object> data = new HashMap<>();

    @JsonAnySetter
    public void setDynamicProperty(String name, Object value) {
        this.data.put(name, value);
    }

    public Map<String, Object> getData() {
        return this.data;
    }
}


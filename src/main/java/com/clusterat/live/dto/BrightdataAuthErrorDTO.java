package com.clusterat.live.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para resposta de erro de autenticação
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrightdataAuthErrorDTO {
    private Boolean success;
    private String message;
    private String errorCode;
    private Long timestamp;

    public static BrightdataAuthErrorDTO unauthorized(String message) {
        return BrightdataAuthErrorDTO.builder()
                .success(false)
                .message(message)
                .errorCode("UNAUTHORIZED")
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static BrightdataAuthErrorDTO forbidden(String message) {
        return BrightdataAuthErrorDTO.builder()
                .success(false)
                .message(message)
                .errorCode("FORBIDDEN")
                .timestamp(System.currentTimeMillis())
                .build();
    }
}


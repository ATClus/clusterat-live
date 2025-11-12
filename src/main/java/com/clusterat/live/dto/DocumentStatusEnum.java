package com.clusterat.live.dto;

import lombok.Getter;

@Getter
public enum DocumentStatusEnum {
    PENDING("pending"),
    PROCESSING("processing"),
    COMPLETED("completed"),
    FAILED("failed");

    private final String value;

    DocumentStatusEnum(String value) {
        this.value = value;
    }

}


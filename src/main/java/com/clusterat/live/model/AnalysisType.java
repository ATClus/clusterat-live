package com.clusterat.live.model;

import lombok.Getter;

@Getter
public enum AnalysisType {
    GOOD("good"),
    BAD("bad"),
    REDUCIBLE("reducible"),
    SUPERFLUOUS("superfluous"),
    UNNECESSARY("unnecessary"),
    ESSENTIAL("essential"),
    INVESTMENT("investment");

    private final String value;

    AnalysisType(String value) {
        this.value = value;
    }

    public static AnalysisType fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (AnalysisType type : AnalysisType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown analysis type: " + value);
    }
}


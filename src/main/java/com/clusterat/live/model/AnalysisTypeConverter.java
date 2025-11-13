package com.clusterat.live.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class AnalysisTypeConverter implements AttributeConverter<AnalysisType, String> {

    @Override
    public String convertToDatabaseColumn(AnalysisType attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getValue();
    }

    @Override
    public AnalysisType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return AnalysisType.fromValue(dbData);
    }
}


package com.clusterat.live.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RentalPropertyDTO {
    private Long id;
    @JsonProperty("property_code")
    private String propertyCode;
    @JsonProperty("property_title")
    private String propertyTitle;
    @JsonProperty("building_name")
    private String buildingName;
    @JsonProperty("property_type")
    private String propertyType;
    @JsonProperty("property_address")
    private String propertyAddress;
    private String neighborhood;
    private String city;
    @JsonProperty("private_area_sqm")
    private BigDecimal privateAreaSqm;
    private Short bedrooms;
    private Short suites;
    @JsonProperty("parking_spaces")
    private Short parkingSpaces;
    @JsonProperty("rental_price")
    private BigDecimal rentalPrice;
    @JsonProperty("condominium_fee")
    private BigDecimal condominiumFee;
    @JsonProperty("iptu_tax")
    private BigDecimal iptuTax;
    @JsonProperty("fire_insurance")
    private BigDecimal fireInsurance;
    @JsonProperty("garbage_collection_fee")
    private BigDecimal garbageCollectionFee;
    @JsonProperty("total_monthly_cost")
    private BigDecimal totalMonthlyCost;
    @JsonProperty("transaction_type")
    private String transactionType;
    @JsonProperty("payment_methods")
    private String paymentMethods;
    @JsonProperty("source_url")
    private String sourceUrl;
    @JsonProperty("scraped_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss[XXX][X]")
    private OffsetDateTime scrapedAt;
}


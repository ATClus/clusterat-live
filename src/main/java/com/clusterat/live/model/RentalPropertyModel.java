package com.clusterat.live.model;

import jakarta.persistence.*;
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
@Entity
@Table(name = "rental_properties", schema = "live")
public class RentalPropertyModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "property_code", nullable = false, length = 50)
    private String propertyCode;

    @Column(name = "property_title")
    private String propertyTitle;

    @Column(name = "building_name")
    private String buildingName;

    @Column(name = "property_type", length = 100)
    private String propertyType;

    @Column(name = "property_address", columnDefinition = "TEXT")
    private String propertyAddress;

    @Column(name = "neighborhood", length = 150)
    private String neighborhood;

    @Column(name = "city", length = 150)
    private String city;

    @Column(name = "private_area_sqm", precision = 10, scale = 2)
    private BigDecimal privateAreaSqm;

    @Column(name = "bedrooms")
    private Short bedrooms;

    @Column(name = "suites")
    private Short suites;

    @Column(name = "parking_spaces")
    private Short parkingSpaces;

    @Column(name = "rental_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal rentalPrice;

    @Column(name = "condominium_fee", precision = 10, scale = 2)
    private BigDecimal condominiumFee;

    @Column(name = "iptu_tax", precision = 10, scale = 2)
    private BigDecimal iptuTax;

    @Column(name = "fire_insurance", precision = 10, scale = 2)
    private BigDecimal fireInsurance;

    @Column(name = "garbage_collection_fee", precision = 10, scale = 2)
    private BigDecimal garbageCollectionFee;

    @Column(name = "total_monthly_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalMonthlyCost;

    @Column(name = "transaction_type", length = 50)
    private String transactionType;

    @Column(name = "payment_methods", columnDefinition = "TEXT")
    private String paymentMethods;

    @Column(name = "source_url", nullable = false, columnDefinition = "TEXT")
    private String sourceUrl;

    @Column(name = "scraped_at")
    private OffsetDateTime scrapedAt;

    @PrePersist
    protected void onCreate() {
        if (scrapedAt == null) {
            scrapedAt = OffsetDateTime.now();
        }
    }
}


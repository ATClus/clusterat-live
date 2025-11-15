package com.clusterat.live.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "brightdata_received_data", schema = "live")
public class BrightdataReceivedDataModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "data", nullable = false, columnDefinition = "jsonb")
    private String data;

    @Column(name = "date_received", nullable = false)
    private OffsetDateTime dateReceived;

    @Column(name = "processed", nullable = false)
    @Builder.Default
    private Boolean processed = false;

    @Column(name = "date_processed")
    private OffsetDateTime dateProcessed;

    @PrePersist
    protected void onCreate() {
        if (dateReceived == null) {
            dateReceived = OffsetDateTime.now();
        }
        if (processed == null) {
            processed = false;
        }
    }
}


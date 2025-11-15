package com.clusterat.live.repository;

import com.clusterat.live.model.BrightdataReceivedDataModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface BrightdataReceivedDataRepository extends JpaRepository<BrightdataReceivedDataModel, Long> {
    List<BrightdataReceivedDataModel> findByProcessedFalse();

    @Query("SELECT b FROM BrightdataReceivedDataModel b WHERE b.processed = false ORDER BY b.dateReceived ASC")
    List<BrightdataReceivedDataModel> findUnprocessedDataOrderByDateReceived();

    List<BrightdataReceivedDataModel> findByDateReceivedBetween(OffsetDateTime startDate, OffsetDateTime endDate);
}


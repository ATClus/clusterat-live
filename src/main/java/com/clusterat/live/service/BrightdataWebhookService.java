package com.clusterat.live.service;

import com.clusterat.live.model.BrightdataReceivedDataModel;
import com.clusterat.live.repository.BrightdataReceivedDataRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@Service
public class BrightdataWebhookService {
    private final BrightdataReceivedDataRepository brightdataRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public BrightdataWebhookService(BrightdataReceivedDataRepository brightdataRepository, ObjectMapper objectMapper) {
        this.brightdataRepository = brightdataRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public BrightdataReceivedDataModel receiveWebhookData(Object webhookData) {
        try {
            log.info("Receiving Brightdata webhook data");

            String jsonData = objectMapper.writeValueAsString(webhookData);

            log.debug("Received JSON: {}", jsonData);

            BrightdataReceivedDataModel model = BrightdataReceivedDataModel.builder()
                    .data(jsonData)
                    .dateReceived(OffsetDateTime.now())
                    .processed(false)
                    .build();

            BrightdataReceivedDataModel savedModel = brightdataRepository.save(model);
            log.info("Brightdata data saved successfully. ID: {}", savedModel.getId());

            return savedModel;
        } catch (Exception e) {
            log.error("Error receiving and saving Brightdata webhook data", e);
            throw new RuntimeException("Error processing Brightdata webhook: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public List<BrightdataReceivedDataModel> getUnprocessedData() {
        log.info("Fetching unprocessed Brightdata data");
        return brightdataRepository.findUnprocessedDataOrderByDateReceived();
    }

    @Transactional
    public void markAsProcessed(Long id) {
        log.info("Marking data as processed. ID: {}", id);
        brightdataRepository.findById(id).ifPresent(data -> {
            data.setProcessed(true);
            data.setDateProcessed(OffsetDateTime.now());
            brightdataRepository.save(data);
            log.info("Data marked as processed. ID: {}", id);
        });
    }

    @Transactional
    public void markAsProcessed(Long id, OffsetDateTime processedDate) {
        log.info("Marking data as processed with specific date. ID: {}", id);
        brightdataRepository.findById(id).ifPresent(data -> {
            data.setProcessed(true);
            data.setDateProcessed(processedDate);
            brightdataRepository.save(data);
            log.info("Data marked as processed. ID: {}", id);
        });
    }

    @Transactional(readOnly = true)
    public List<BrightdataReceivedDataModel> getDataByDateRange(OffsetDateTime startDate, OffsetDateTime endDate) {
        log.info("Fetching Brightdata data between {} and {}", startDate, endDate);
        return brightdataRepository.findByDateReceivedBetween(startDate, endDate);
    }
}

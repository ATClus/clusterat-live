package com.clusterat.live.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DocumentCleanupSchedulerService {

    private final DocProcessorService docProcessorService;

    @Value("${document.cleanup.scheduler.enabled:true}")
    private boolean schedulerEnabled;

    @Value("${document.cleanup.scheduler.max-age-minutes:60}")
    private int maxAgeMinutes;

    @Autowired
    public DocumentCleanupSchedulerService(DocProcessorService docProcessorService) {
        this.docProcessorService = docProcessorService;
    }

    @Scheduled(fixedDelayString = "${document.cleanup.scheduler.interval-ms:1800000}") // 30 minutes
    public void cleanupOldDocuments() {
        if (!schedulerEnabled) {
            log.debug("Document cleanup scheduler is disabled");
            return;
        }

        try {
            log.info("Running automatic cleanup of old documents");
            docProcessorService.cleanupOldDocuments(maxAgeMinutes);
        } catch (Exception e) {
            log.error("Error executing automatic document cleanup: {}", e.getMessage(), e);
        }
    }
}

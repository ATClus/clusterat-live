package com.clusterat.live.controller;

import com.clusterat.live.model.BrightdataReceivedDataModel;
import com.clusterat.live.service.BrightdataWebhookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/v1/webhooks/brightdata")
public class BrightdataWebhookController {
    private final BrightdataWebhookService brightdataWebhookService;

    @Autowired
    public BrightdataWebhookController(BrightdataWebhookService brightdataWebhookService) {
        this.brightdataWebhookService = brightdataWebhookService;
    }

    /**
     * Endpoint to receive Brightdata webhook data
     * POST /api/v1/webhooks/brightdata/receive
     *
     * @param webhookData Data sent by Brightdata (accepts Object to support arrays and objects)
     * @return Response with saved record ID
     */
    @PostMapping("/receive")
    public ResponseEntity<Map<String, Object>> receiveWebhookData(@RequestBody Object webhookData) {
        try {
            log.info("Brightdata webhook received");
            BrightdataReceivedDataModel savedData = brightdataWebhookService.receiveWebhookData(webhookData);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Data received and saved successfully");
            response.put("id", savedData.getId());
            response.put("dateReceived", savedData.getDateReceived());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error processing Brightdata webhook", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error processing webhook: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Endpoint to fetch unprocessed data
     * GET /api/v1/webhooks/brightdata/unprocessed
     * REQUIRED AUTHENTICATION:
     * Header: X-API-Key: [your-api-key]
     *
     * @return List of unprocessed data
     */
    @GetMapping("/unprocessed")
    public ResponseEntity<Map<String, Object>> getUnprocessedData() {
        try {
            log.info("Fetching unprocessed Brightdata data");
            List<BrightdataReceivedDataModel> unprocessedData = brightdataWebhookService.getUnprocessedData();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", unprocessedData.size());
            response.put("data", unprocessedData);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching unprocessed data", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error fetching data: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Endpoint to mark data as processed
     * PUT /api/v1/webhooks/brightdata/{id}/mark-processed
     * REQUIRED AUTHENTICATION:
     * Header: X-API-Key: [your-api-key]
     *
     * @param id ID of the record to be marked as processed
     * @return Response with success or error
     */
    @PutMapping("/{id}/mark-processed")
    public ResponseEntity<Map<String, Object>> markAsProcessed(@PathVariable Long id) {
        try {
            log.info("Marking data as processed. ID: {}", id);
            brightdataWebhookService.markAsProcessed(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Data marked as processed");
            response.put("id", id);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error marking data as processed", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error marking as processed: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Webhook health check
     * GET /api/v1/webhooks/brightdata/health
     *
     * @return Webhook status
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Brightdata Webhook");
        response.put("message", "Brightdata webhook is operational");

        return ResponseEntity.ok(response);
    }
}

package com.clusterat.live.controller;

import com.clusterat.live.dto.BrightdataWebhookDTO;
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
@RequestMapping("/api/v1/webhooks/brightdata")
public class BrightdataWebhookController {
    private final BrightdataWebhookService brightdataWebhookService;

    @Autowired
    public BrightdataWebhookController(BrightdataWebhookService brightdataWebhookService) {
        this.brightdataWebhookService = brightdataWebhookService;
    }

    /**
     * Endpoint para receber dados do webhook Brightdata
     * POST /api/v1/webhooks/brightdata/receive
     *
     * @param webhookData Dados enviados pelo Brightdata
     * @return Resposta com ID do registro salvo
     */
    @PostMapping("/receive")
    public ResponseEntity<Map<String, Object>> receiveWebhookData(@RequestBody BrightdataWebhookDTO webhookData) {
        try {
            log.info("Webhook Brightdata recebido");
            BrightdataReceivedDataModel savedData = brightdataWebhookService.receiveWebhookData(webhookData);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Dados recebidos e salvos com sucesso");
            response.put("id", savedData.getId());
            response.put("dateReceived", savedData.getDateReceived());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Erro ao processar webhook Brightdata", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Erro ao processar webhook: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Endpoint para buscar dados não processados
     * GET /api/v1/webhooks/brightdata/unprocessed
     * AUTENTICAÇÃO REQUERIDA:
     * Header: X-API-Key: [sua-chave-api]
     *
     * @return Lista de dados não processados
     */
    @GetMapping("/unprocessed")
    public ResponseEntity<Map<String, Object>> getUnprocessedData() {
        try {
            log.info("Buscando dados não processados do Brightdata");
            List<BrightdataReceivedDataModel> unprocessedData = brightdataWebhookService.getUnprocessedData();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", unprocessedData.size());
            response.put("data", unprocessedData);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erro ao buscar dados não processados", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Erro ao buscar dados: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Endpoint para receber dados do webhook Brightdata
     * POST /api/v1/webhooks/brightdata/receive
     * AUTENTICAÇÃO REQUERIDA:
     * Header: X-API-Key: [sua-chave-api]
     *
     * @param webhookData Dados enviados pelo Brightdata
     * @return Resposta com ID do registro salvo
     */
    @PutMapping("/{id}/mark-processed")
    public ResponseEntity<Map<String, Object>> markAsProcessed(@PathVariable Long id) {
        try {
            log.info("Marcando dados como processados. ID: {}", id);
            brightdataWebhookService.markAsProcessed(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Dados marcados como processados");
            response.put("id", id);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erro ao marcar dados como processados", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Erro ao marcar como processado: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Health check do webhook
     * GET /api/v1/webhooks/brightdata/health
     *
     * @return Status do webhook
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Brightdata Webhook");
        response.put("message", "Webhook Brightdata está operacional");

        return ResponseEntity.ok(response);
    }
}


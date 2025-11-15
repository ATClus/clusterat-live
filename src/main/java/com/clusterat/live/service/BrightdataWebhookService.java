package com.clusterat.live.service;

import com.clusterat.live.dto.BrightdataWebhookDTO;
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
    public BrightdataReceivedDataModel receiveWebhookData(BrightdataWebhookDTO webhookData) {
        try {
            log.info("Recebendo dados do webhook Brightdata");

            // Converter o DTO para JSON string
            String jsonData = objectMapper.writeValueAsString(webhookData.getData());

            // Criar o modelo de dados
            BrightdataReceivedDataModel model = BrightdataReceivedDataModel.builder()
                    .data(jsonData)
                    .dateReceived(OffsetDateTime.now())
                    .processed(false)
                    .build();

            // Salvar no banco de dados
            BrightdataReceivedDataModel savedModel = brightdataRepository.save(model);
            log.info("Dados do Brightdata salvos com sucesso. ID: {}", savedModel.getId());

            return savedModel;
        } catch (Exception e) {
            log.error("Erro ao receber e salvar dados do webhook Brightdata", e);
            throw new RuntimeException("Erro ao processar webhook Brightdata: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public List<BrightdataReceivedDataModel> getUnprocessedData() {
        log.info("Buscando dados não processados do Brightdata");
        return brightdataRepository.findUnprocessedDataOrderByDateReceived();
    }

    @Transactional
    public void markAsProcessed(Long id) {
        log.info("Marcando dados como processados. ID: {}", id);
        brightdataRepository.findById(id).ifPresent(data -> {
            data.setProcessed(true);
            data.setDateProcessed(OffsetDateTime.now());
            brightdataRepository.save(data);
            log.info("Dados marcados como processados. ID: {}", id);
        });
    }

    @Transactional
    public void markAsProcessed(Long id, OffsetDateTime processedDate) {
        log.info("Marcando dados como processados com data específica. ID: {}", id);
        brightdataRepository.findById(id).ifPresent(data -> {
            data.setProcessed(true);
            data.setDateProcessed(processedDate);
            brightdataRepository.save(data);
            log.info("Dados marcados como processados. ID: {}", id);
        });
    }

    @Transactional(readOnly = true)
    public List<BrightdataReceivedDataModel> getDataByDateRange(OffsetDateTime startDate, OffsetDateTime endDate) {
        log.info("Buscando dados Brightdata entre {} e {}", startDate, endDate);
        return brightdataRepository.findByDateReceivedBetween(startDate, endDate);
    }
}


package com.clusterat.live.service;

import com.clusterat.live.dto.BrightdataWebhookDTO;
import com.clusterat.live.model.BrightdataReceivedDataModel;
import com.clusterat.live.repository.BrightdataReceivedDataRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class BrightdataWebhookServiceTest {

    private BrightdataWebhookService service;

    @Mock
    private BrightdataReceivedDataRepository repository;

    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new BrightdataWebhookService(repository, objectMapper);
    }

    @Test
    void testReceiveWebhookData_Success() throws Exception {
        // Arrange
        BrightdataWebhookDTO webhookDTO = new BrightdataWebhookDTO();
        Map<String, Object> data = new HashMap<>();
        data.put("field1", "value1");
        data.put("field2", "value2");
        webhookDTO.setData(data);

        String jsonData = "{\"field1\":\"value1\",\"field2\":\"value2\"}";
        when(objectMapper.writeValueAsString(any())).thenReturn(jsonData);

        BrightdataReceivedDataModel savedModel = BrightdataReceivedDataModel.builder()
                .id(1L)
                .data(jsonData)
                .dateReceived(OffsetDateTime.now())
                .processed(false)
                .build();

        when(repository.save(any(BrightdataReceivedDataModel.class))).thenReturn(savedModel);

        // Act
        BrightdataReceivedDataModel result = service.receiveWebhookData(webhookDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(jsonData, result.getData());
        assertFalse(result.getProcessed());
        assertNull(result.getDateProcessed());
        verify(repository, times(1)).save(any(BrightdataReceivedDataModel.class));
    }

    @Test
    void testReceiveWebhookData_EmptyData() throws Exception {
        // Arrange
        BrightdataWebhookDTO webhookDTO = new BrightdataWebhookDTO();
        String jsonData = "{}";
        when(objectMapper.writeValueAsString(any())).thenReturn(jsonData);

        BrightdataReceivedDataModel savedModel = BrightdataReceivedDataModel.builder()
                .id(1L)
                .data(jsonData)
                .dateReceived(OffsetDateTime.now())
                .processed(false)
                .build();

        when(repository.save(any(BrightdataReceivedDataModel.class))).thenReturn(savedModel);

        // Act
        BrightdataReceivedDataModel result = service.receiveWebhookData(webhookDTO);

        // Assert
        assertNotNull(result);
        assertEquals(jsonData, result.getData());
        verify(repository, times(1)).save(any(BrightdataReceivedDataModel.class));
    }

    @Test
    void testGetUnprocessedData() {
        // Arrange
        BrightdataReceivedDataModel data1 = BrightdataReceivedDataModel.builder()
                .id(1L)
                .data("{\"test\":\"data1\"}")
                .dateReceived(OffsetDateTime.now())
                .processed(false)
                .build();

        BrightdataReceivedDataModel data2 = BrightdataReceivedDataModel.builder()
                .id(2L)
                .data("{\"test\":\"data2\"}")
                .dateReceived(OffsetDateTime.now())
                .processed(false)
                .build();

        List<BrightdataReceivedDataModel> unprocessedList = Arrays.asList(data1, data2);
        when(repository.findUnprocessedDataOrderByDateReceived()).thenReturn(unprocessedList);

        // Act
        List<BrightdataReceivedDataModel> result = service.getUnprocessedData();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        verify(repository, times(1)).findUnprocessedDataOrderByDateReceived();
    }

    @Test
    void testGetUnprocessedData_Empty() {
        // Arrange
        when(repository.findUnprocessedDataOrderByDateReceived()).thenReturn(Arrays.asList());

        // Act
        List<BrightdataReceivedDataModel> result = service.getUnprocessedData();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository, times(1)).findUnprocessedDataOrderByDateReceived();
    }

    @Test
    void testMarkAsProcessed() {
        // Arrange
        Long id = 1L;
        BrightdataReceivedDataModel data = BrightdataReceivedDataModel.builder()
                .id(id)
                .data("{\"test\":\"data\"}")
                .dateReceived(OffsetDateTime.now())
                .processed(false)
                .build();

        when(repository.findById(id)).thenReturn(Optional.of(data));
        when(repository.save(any(BrightdataReceivedDataModel.class))).thenReturn(data);

        // Act
        service.markAsProcessed(id);

        // Assert
        assertTrue(data.getProcessed());
        assertNotNull(data.getDateProcessed());
        verify(repository, times(1)).findById(id);
        verify(repository, times(1)).save(any(BrightdataReceivedDataModel.class));
    }

    @Test
    void testMarkAsProcessed_WithCustomDate() {
        // Arrange
        Long id = 1L;
        OffsetDateTime customDate = OffsetDateTime.now().minusHours(1);
        BrightdataReceivedDataModel data = BrightdataReceivedDataModel.builder()
                .id(id)
                .data("{\"test\":\"data\"}")
                .dateReceived(OffsetDateTime.now())
                .processed(false)
                .build();

        when(repository.findById(id)).thenReturn(Optional.of(data));
        when(repository.save(any(BrightdataReceivedDataModel.class))).thenReturn(data);

        // Act
        service.markAsProcessed(id, customDate);

        // Assert
        assertTrue(data.getProcessed());
        assertEquals(customDate, data.getDateProcessed());
        verify(repository, times(1)).findById(id);
        verify(repository, times(1)).save(any(BrightdataReceivedDataModel.class));
    }

    @Test
    void testMarkAsProcessed_NotFound() {
        // Arrange
        Long id = 999L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        // Act
        service.markAsProcessed(id);

        // Assert
        verify(repository, times(1)).findById(id);
        verify(repository, never()).save(any(BrightdataReceivedDataModel.class));
    }

    @Test
    void testGetDataByDateRange() {
        // Arrange
        OffsetDateTime startDate = OffsetDateTime.now().minusDays(1);
        OffsetDateTime endDate = OffsetDateTime.now();

        BrightdataReceivedDataModel data = BrightdataReceivedDataModel.builder()
                .id(1L)
                .data("{\"test\":\"data\"}")
                .dateReceived(OffsetDateTime.now())
                .processed(false)
                .build();

        List<BrightdataReceivedDataModel> dataList = Arrays.asList(data);
        when(repository.findByDateReceivedBetween(startDate, endDate)).thenReturn(dataList);

        // Act
        List<BrightdataReceivedDataModel> result = service.getDataByDateRange(startDate, endDate);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repository, times(1)).findByDateReceivedBetween(startDate, endDate);
    }
}


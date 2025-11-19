package com.clusterat.live.mcp;

import com.clusterat.live.dto.RentalPropertyDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class RentalPropertyMCPService {
    private final com.clusterat.live.service.RentalPropertyService rentalPropertyService;

    public RentalPropertyMCPService(com.clusterat.live.service.RentalPropertyService rentalPropertyService) {
        this.rentalPropertyService = rentalPropertyService;
    }

    @Tool(description = "Get the lastest 10 rows from rental_properties table")
    public ResponseEntity<List<RentalPropertyDTO>> getLatestRentalProperties() {
        try {
            List<RentalPropertyDTO> properties = rentalPropertyService.getLatestProperties();
            return ResponseEntity.ok(properties == null ? Collections.emptyList() : properties);
        } catch (Exception e) {
            log.error("Error fetching latest rental properties ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }
}

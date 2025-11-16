package com.clusterat.live.controller;

import com.clusterat.live.dto.AnalysisResponseDTO;
import com.clusterat.live.dto.RentalPropertyDTO;
import com.clusterat.live.service.RentalPropertyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/v1/rental-properties")
public class RentalPropertyController {
    private final RentalPropertyService rentalPropertyService;

    @Autowired
    public RentalPropertyController(RentalPropertyService rentalPropertyService) {
        this.rentalPropertyService = rentalPropertyService;
    }

    @GetMapping
    public ResponseEntity<AnalysisResponseDTO> getAllProperties() {
        log.info("GET request to fetch all rental properties");
        try {
            List<RentalPropertyDTO> properties = rentalPropertyService.getAllProperties();
            return ResponseEntity.ok(AnalysisResponseDTO.builder()
                    .success(true)
                    .message("Rental properties retrieved successfully")
                    .data(properties)
                    .build());
        } catch (Exception e) {
            log.error("Error fetching rental properties", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AnalysisResponseDTO.builder()
                            .success(false)
                            .message("Error fetching rental properties")
                            .build());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnalysisResponseDTO> getPropertyById(@PathVariable Long id) {
        log.info("GET request to fetch rental property by id: {}", id);
        try {
            Optional<RentalPropertyDTO> property = rentalPropertyService.getPropertyById(id);
            return property.map(dto -> ResponseEntity.ok(AnalysisResponseDTO.builder()
                    .success(true)
                    .message("Rental property retrieved successfully")
                    .data(dto)
                    .build())).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(AnalysisResponseDTO.builder()
                            .success(false)
                            .message("Rental property not found")
                            .build()));
        } catch (Exception e) {
            log.error("Error fetching rental property by id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AnalysisResponseDTO.builder()
                            .success(false)
                            .message("Error fetching rental property")
                            .build());
        }
    }

    @GetMapping("/neighborhood/{neighborhood}")
    public ResponseEntity<AnalysisResponseDTO> getPropertiesByNeighborhood(@PathVariable String neighborhood) {
        log.info("GET request to fetch rental properties by neighborhood: {}", neighborhood);
        try {
            List<RentalPropertyDTO> properties = rentalPropertyService.getPropertiesByNeighborhood(neighborhood);
            return ResponseEntity.ok(AnalysisResponseDTO.builder()
                    .success(true)
                    .message("Rental properties retrieved successfully")
                    .data(properties)
                    .build());
        } catch (Exception e) {
            log.error("Error fetching rental properties by neighborhood: {}", neighborhood, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AnalysisResponseDTO.builder()
                            .success(false)
                            .message("Error fetching rental properties")
                            .build());
        }
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<AnalysisResponseDTO> getPropertiesByCity(@PathVariable String city) {
        log.info("GET request to fetch rental properties by city: {}", city);
        try {
            List<RentalPropertyDTO> properties = rentalPropertyService.getPropertiesByCity(city);
            return ResponseEntity.ok(AnalysisResponseDTO.builder()
                    .success(true)
                    .message("Rental properties retrieved successfully")
                    .data(properties)
                    .build());
        } catch (Exception e) {
            log.error("Error fetching rental properties by city: {}", city, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AnalysisResponseDTO.builder()
                            .success(false)
                            .message("Error fetching rental properties")
                            .build());
        }
    }

    @GetMapping("/type/{propertyType}")
    public ResponseEntity<AnalysisResponseDTO> getPropertiesByType(@PathVariable String propertyType) {
        log.info("GET request to fetch rental properties by type: {}", propertyType);
        try {
            List<RentalPropertyDTO> properties = rentalPropertyService.getPropertiesByType(propertyType);
            return ResponseEntity.ok(AnalysisResponseDTO.builder()
                    .success(true)
                    .message("Rental properties retrieved successfully")
                    .data(properties)
                    .build());
        } catch (Exception e) {
            log.error("Error fetching rental properties by type: {}", propertyType, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AnalysisResponseDTO.builder()
                            .success(false)
                            .message("Error fetching rental properties")
                            .build());
        }
    }

    @GetMapping("/price-range")
    public ResponseEntity<AnalysisResponseDTO> getPropertiesByPriceRange(
            @RequestParam BigDecimal minCost,
            @RequestParam BigDecimal maxCost) {
        log.info("GET request to fetch rental properties by price range: {} - {}", minCost, maxCost);
        try {
            List<RentalPropertyDTO> properties = rentalPropertyService.getPropertiesByPriceRange(minCost, maxCost);
            return ResponseEntity.ok(AnalysisResponseDTO.builder()
                    .success(true)
                    .message("Rental properties retrieved successfully")
                    .data(properties)
                    .build());
        } catch (Exception e) {
            log.error("Error fetching rental properties by price range", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AnalysisResponseDTO.builder()
                            .success(false)
                            .message("Error fetching rental properties")
                            .build());
        }
    }

    @PostMapping
    public ResponseEntity<AnalysisResponseDTO> createProperty(@RequestBody RentalPropertyDTO propertyDTO) {
        log.info("POST request to create rental property");
        try {
            RentalPropertyDTO created = rentalPropertyService.createProperty(propertyDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(AnalysisResponseDTO.builder()
                            .success(true)
                            .message("Rental property created successfully")
                            .data(created)
                            .build());
        } catch (IllegalArgumentException e) {
            log.error("Validation error creating rental property", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(AnalysisResponseDTO.builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Error creating rental property", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AnalysisResponseDTO.builder()
                            .success(false)
                            .message("Error creating rental property")
                            .build());
        }
    }

    @PostMapping("/bulk")
    public ResponseEntity<AnalysisResponseDTO> createProperties(@RequestBody List<RentalPropertyDTO> propertyDTOs) {
        log.info("POST request to create {} rental properties", propertyDTOs.size());
        try {
            List<RentalPropertyDTO> created = rentalPropertyService.createProperties(propertyDTOs);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(AnalysisResponseDTO.builder()
                            .success(true)
                            .message(String.format("Created %d out of %d rental properties",
                                    created.size(), propertyDTOs.size()))
                            .data(created)
                            .build());
        } catch (Exception e) {
            log.error("Error creating rental properties", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AnalysisResponseDTO.builder()
                            .success(false)
                            .message("Error creating rental properties")
                            .build());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnalysisResponseDTO> updateProperty(
            @PathVariable Long id,
            @RequestBody RentalPropertyDTO propertyDTO) {
        log.info("PUT request to update rental property with id: {}", id);
        try {
            RentalPropertyDTO updated = rentalPropertyService.updateProperty(id, propertyDTO);
            return ResponseEntity.ok(AnalysisResponseDTO.builder()
                    .success(true)
                    .message("Rental property updated successfully")
                    .data(updated)
                    .build());
        } catch (IllegalArgumentException e) {
            log.error("Validation error updating rental property", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(AnalysisResponseDTO.builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Error updating rental property with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AnalysisResponseDTO.builder()
                            .success(false)
                            .message("Error updating rental property")
                            .build());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AnalysisResponseDTO> deleteProperty(@PathVariable Long id) {
        log.info("DELETE request to delete rental property with id: {}", id);
        try {
            rentalPropertyService.deleteProperty(id);
            return ResponseEntity.ok(AnalysisResponseDTO.builder()
                    .success(true)
                    .message("Rental property deleted successfully")
                    .build());
        } catch (IllegalArgumentException e) {
            log.error("Validation error deleting rental property", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(AnalysisResponseDTO.builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Error deleting rental property with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AnalysisResponseDTO.builder()
                            .success(false)
                            .message("Error deleting rental property")
                            .build());
        }
    }
}


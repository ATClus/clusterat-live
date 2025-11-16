package com.clusterat.live.service;

import com.clusterat.live.dto.RentalPropertyDTO;
import com.clusterat.live.model.RentalPropertyModel;
import com.clusterat.live.repository.RentalPropertyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RentalPropertyService {
    private final RentalPropertyRepository rentalPropertyRepository;

    @Autowired
    public RentalPropertyService(RentalPropertyRepository rentalPropertyRepository) {
        this.rentalPropertyRepository = rentalPropertyRepository;
    }

    public List<RentalPropertyDTO> getAllProperties() {
        log.info("Fetching all rental properties");
        return rentalPropertyRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<RentalPropertyDTO> getPropertyById(Long id) {
        log.info("Fetching rental property with id: {}", id);
        return rentalPropertyRepository.findById(id)
                .map(this::convertToDTO);
    }

    public List<RentalPropertyDTO> getPropertiesByNeighborhood(String neighborhood) {
        log.info("Fetching rental properties by neighborhood: {}", neighborhood);
        return rentalPropertyRepository.findByNeighborhood(neighborhood).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<RentalPropertyDTO> getPropertiesByCity(String city) {
        log.info("Fetching rental properties by city: {}", city);
        return rentalPropertyRepository.findByCity(city).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<RentalPropertyDTO> getPropertiesByType(String propertyType) {
        log.info("Fetching rental properties by type: {}", propertyType);
        return rentalPropertyRepository.findByPropertyType(propertyType).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<RentalPropertyDTO> getPropertiesByPriceRange(BigDecimal minCost, BigDecimal maxCost) {
        log.info("Fetching rental properties with cost between {} and {}", minCost, maxCost);
        return rentalPropertyRepository.findByTotalMonthlyCostBetween(minCost, maxCost).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public RentalPropertyDTO createProperty(RentalPropertyDTO dto) {
        log.info("Creating new rental property with code: {}", dto.getPropertyCode());
        Optional<RentalPropertyModel> existing = rentalPropertyRepository
                .findByPropertyCodeAndSourceUrl(dto.getPropertyCode(), dto.getSourceUrl());

        if (existing.isPresent()) {
            log.warn("Property with code {} and source URL {} already exists",
                    dto.getPropertyCode(), dto.getSourceUrl());
            throw new IllegalArgumentException("Property already exists");
        }

        RentalPropertyModel model = convertToModel(dto);
        RentalPropertyModel saved = rentalPropertyRepository.save(model);
        log.info("Rental property created with id: {}", saved.getId());
        return convertToDTO(saved);
    }

    @Transactional
    public List<RentalPropertyDTO> createProperties(List<RentalPropertyDTO> dtos) {
        log.info("Creating {} rental properties", dtos.size());
        return dtos.stream()
                .map(dto -> {
                    try {
                        return createProperty(dto);
                    } catch (IllegalArgumentException e) {
                        log.warn("Skipping duplicate property with code: {}", dto.getPropertyCode());
                        return null;
                    }
                })
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Transactional
    public RentalPropertyDTO updateProperty(Long id, RentalPropertyDTO dto) {
        log.info("Updating rental property with id: {}", id);
        RentalPropertyModel model = rentalPropertyRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Property not found with id: {}", id);
                    return new IllegalArgumentException("Property not found");
                });

        updateModelFromDTO(model, dto);
        RentalPropertyModel updated = rentalPropertyRepository.save(model);
        log.info("Rental property updated with id: {}", updated.getId());
        return convertToDTO(updated);
    }

    @Transactional
    public void deleteProperty(Long id) {
        log.info("Deleting rental property with id: {}", id);
        if (!rentalPropertyRepository.existsById(id)) {
            log.error("Property not found with id: {}", id);
            throw new IllegalArgumentException("Property not found");
        }
        rentalPropertyRepository.deleteById(id);
        log.info("Rental property deleted with id: {}", id);
    }

    private RentalPropertyDTO convertToDTO(RentalPropertyModel model) {
        return RentalPropertyDTO.builder()
                .id(model.getId())
                .propertyCode(model.getPropertyCode())
                .propertyTitle(model.getPropertyTitle())
                .buildingName(model.getBuildingName())
                .propertyType(model.getPropertyType())
                .propertyAddress(model.getPropertyAddress())
                .neighborhood(model.getNeighborhood())
                .city(model.getCity())
                .privateAreaSqm(model.getPrivateAreaSqm())
                .bedrooms(model.getBedrooms())
                .suites(model.getSuites())
                .parkingSpaces(model.getParkingSpaces())
                .rentalPrice(model.getRentalPrice())
                .condominiumFee(model.getCondominiumFee())
                .iptuTax(model.getIptuTax())
                .fireInsurance(model.getFireInsurance())
                .garbageCollectionFee(model.getGarbageCollectionFee())
                .totalMonthlyCost(model.getTotalMonthlyCost())
                .transactionType(model.getTransactionType())
                .paymentMethods(model.getPaymentMethods())
                .sourceUrl(model.getSourceUrl())
                .scrapedAt(model.getScrapedAt())
                .build();
    }

    private RentalPropertyModel convertToModel(RentalPropertyDTO dto) {
        return RentalPropertyModel.builder()
                .propertyCode(dto.getPropertyCode())
                .propertyTitle(dto.getPropertyTitle())
                .buildingName(dto.getBuildingName())
                .propertyType(dto.getPropertyType())
                .propertyAddress(dto.getPropertyAddress())
                .neighborhood(dto.getNeighborhood())
                .city(dto.getCity())
                .privateAreaSqm(dto.getPrivateAreaSqm())
                .bedrooms(dto.getBedrooms())
                .suites(dto.getSuites())
                .parkingSpaces(dto.getParkingSpaces())
                .rentalPrice(dto.getRentalPrice())
                .condominiumFee(dto.getCondominiumFee())
                .iptuTax(dto.getIptuTax())
                .fireInsurance(dto.getFireInsurance())
                .garbageCollectionFee(dto.getGarbageCollectionFee())
                .totalMonthlyCost(dto.getTotalMonthlyCost())
                .transactionType(dto.getTransactionType())
                .paymentMethods(dto.getPaymentMethods())
                .sourceUrl(dto.getSourceUrl())
                .scrapedAt(dto.getScrapedAt())
                .build();
    }

    private void updateModelFromDTO(RentalPropertyModel model, RentalPropertyDTO dto) {
        if (dto.getPropertyCode() != null) {
            model.setPropertyCode(dto.getPropertyCode());
        }
        if (dto.getPropertyTitle() != null) {
            model.setPropertyTitle(dto.getPropertyTitle());
        }
        if (dto.getBuildingName() != null) {
            model.setBuildingName(dto.getBuildingName());
        }
        if (dto.getPropertyType() != null) {
            model.setPropertyType(dto.getPropertyType());
        }
        if (dto.getPropertyAddress() != null) {
            model.setPropertyAddress(dto.getPropertyAddress());
        }
        if (dto.getNeighborhood() != null) {
            model.setNeighborhood(dto.getNeighborhood());
        }
        if (dto.getCity() != null) {
            model.setCity(dto.getCity());
        }
        if (dto.getPrivateAreaSqm() != null) {
            model.setPrivateAreaSqm(dto.getPrivateAreaSqm());
        }
        if (dto.getBedrooms() != null) {
            model.setBedrooms(dto.getBedrooms());
        }
        if (dto.getSuites() != null) {
            model.setSuites(dto.getSuites());
        }
        if (dto.getParkingSpaces() != null) {
            model.setParkingSpaces(dto.getParkingSpaces());
        }
        if (dto.getRentalPrice() != null) {
            model.setRentalPrice(dto.getRentalPrice());
        }
        if (dto.getCondominiumFee() != null) {
            model.setCondominiumFee(dto.getCondominiumFee());
        }
        if (dto.getIptuTax() != null) {
            model.setIptuTax(dto.getIptuTax());
        }
        if (dto.getFireInsurance() != null) {
            model.setFireInsurance(dto.getFireInsurance());
        }
        if (dto.getGarbageCollectionFee() != null) {
            model.setGarbageCollectionFee(dto.getGarbageCollectionFee());
        }
        if (dto.getTotalMonthlyCost() != null) {
            model.setTotalMonthlyCost(dto.getTotalMonthlyCost());
        }
        if (dto.getTransactionType() != null) {
            model.setTransactionType(dto.getTransactionType());
        }
        if (dto.getPaymentMethods() != null) {
            model.setPaymentMethods(dto.getPaymentMethods());
        }
        if (dto.getSourceUrl() != null) {
            model.setSourceUrl(dto.getSourceUrl());
        }
        if (dto.getScrapedAt() != null) {
            model.setScrapedAt(dto.getScrapedAt());
        }
    }
}


package com.clusterat.live.repository;

import com.clusterat.live.model.RentalPropertyModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface RentalPropertyRepository extends JpaRepository<RentalPropertyModel, Long> {
    Optional<RentalPropertyModel> findByPropertyCodeAndSourceUrl(String propertyCode, String sourceUrl);
    List<RentalPropertyModel> findByNeighborhood(String neighborhood);
    List<RentalPropertyModel> findByCity(String city);
    List<RentalPropertyModel> findByPropertyType(String propertyType);
    List<RentalPropertyModel> findByTotalMonthlyCostBetween(BigDecimal minCost, BigDecimal maxCost);
    List<RentalPropertyModel> findTop10ByOrderByScrapedAtDesc();
    boolean existsByPropertyCode(String propertyCode);
    void deleteByPropertyCode(String propertyCode);
}


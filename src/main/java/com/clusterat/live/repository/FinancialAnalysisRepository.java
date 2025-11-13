package com.clusterat.live.repository;

import com.clusterat.live.model.AnalysisType;
import com.clusterat.live.model.FinancialAnalysisModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FinancialAnalysisRepository extends JpaRepository<FinancialAnalysisModel, Long> {
    List<FinancialAnalysisModel> findByCategoryId(Integer categoryId);
    List<FinancialAnalysisModel> findByAnalysisType(AnalysisType analysisType);
    List<FinancialAnalysisModel> findBySourceTransactionId(String sourceTransactionId);
}


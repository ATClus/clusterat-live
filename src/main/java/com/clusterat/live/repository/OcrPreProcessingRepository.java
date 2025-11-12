package com.clusterat.live.repository;

import com.clusterat.live.model.OcrPreProcessingModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OcrPreProcessingRepository extends JpaRepository<OcrPreProcessingModel, String> {
    Optional<OcrPreProcessingModel> findByDocumentId(String documentId);
}


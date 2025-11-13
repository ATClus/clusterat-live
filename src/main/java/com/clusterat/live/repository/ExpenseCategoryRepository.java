package com.clusterat.live.repository;

import com.clusterat.live.model.ExpenseCategoryModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategoryModel, Integer> {
    Optional<ExpenseCategoryModel> findByName(String name);
}


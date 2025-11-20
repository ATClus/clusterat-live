package com.clusterat.live.repository;

import com.clusterat.live.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Long> {
    Optional<UserModel> findByWppId(String wppId);
    List<UserModel> findByNameContainingIgnoreCase(String name);
}


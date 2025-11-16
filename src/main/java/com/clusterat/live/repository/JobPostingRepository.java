package com.clusterat.live.repository;

import com.clusterat.live.model.JobPostingModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobPostingRepository extends JpaRepository<JobPostingModel, Long> {
    Optional<JobPostingModel> findByJobPostingId(String jobPostingId);
    List<JobPostingModel> findByCompanyName(String companyName);
    List<JobPostingModel> findByJobLocation(String jobLocation);
    List<JobPostingModel> findBySeniorityLevel(String seniorityLevel);
    List<JobPostingModel> findByEmploymentType(String employmentType);
    List<JobPostingModel> findByJobTitleContainingIgnoreCase(String jobTitle);
}


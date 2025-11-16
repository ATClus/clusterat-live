package com.clusterat.live.service;

import com.clusterat.live.dto.JobPostingDTO;
import com.clusterat.live.model.JobPostingModel;
import com.clusterat.live.repository.JobPostingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class JobPostingService {
    private final JobPostingRepository jobPostingRepository;

    @Autowired
    public JobPostingService(JobPostingRepository jobPostingRepository) {
        this.jobPostingRepository = jobPostingRepository;
    }

    public List<JobPostingDTO> getAllJobPostings() {
        log.info("Fetching all job postings");
        return jobPostingRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<JobPostingDTO> getJobPostingById(Long id) {
        log.info("Fetching job posting with id: {}", id);
        return jobPostingRepository.findById(id)
                .map(this::convertToDTO);
    }

    public Optional<JobPostingDTO> getJobPostingByJobPostingId(String jobPostingId) {
        log.info("Fetching job posting with jobPostingId: {}", jobPostingId);
        return jobPostingRepository.findByJobPostingId(jobPostingId)
                .map(this::convertToDTO);
    }

    public List<JobPostingDTO> getJobPostingsByCompany(String companyName) {
        log.info("Fetching job postings by company: {}", companyName);
        return jobPostingRepository.findByCompanyName(companyName).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<JobPostingDTO> getJobPostingsByLocation(String jobLocation) {
        log.info("Fetching job postings by location: {}", jobLocation);
        return jobPostingRepository.findByJobLocation(jobLocation).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<JobPostingDTO> getJobPostingsBySeniority(String seniorityLevel) {
        log.info("Fetching job postings by seniority: {}", seniorityLevel);
        return jobPostingRepository.findBySeniorityLevel(seniorityLevel).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<JobPostingDTO> getJobPostingsByEmploymentType(String employmentType) {
        log.info("Fetching job postings by employment type: {}", employmentType);
        return jobPostingRepository.findByEmploymentType(employmentType).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<JobPostingDTO> searchJobPostingsByTitle(String jobTitle) {
        log.info("Searching job postings by title: {}", jobTitle);
        return jobPostingRepository.findByJobTitleContainingIgnoreCase(jobTitle).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public JobPostingDTO createJobPosting(JobPostingDTO dto) {
        log.info("Creating new job posting with id: {}", dto.getJobPostingId());
        Optional<JobPostingModel> existing = jobPostingRepository
                .findByJobPostingId(dto.getJobPostingId());

        if (existing.isPresent()) {
            log.warn("Job posting with id {} already exists", dto.getJobPostingId());
            throw new IllegalArgumentException("Job posting already exists");
        }

        JobPostingModel model = convertToModel(dto);
        JobPostingModel saved = jobPostingRepository.save(model);
        log.info("Job posting created with id: {}", saved.getId());
        return convertToDTO(saved);
    }

    @Transactional
    public List<JobPostingDTO> createJobPostings(List<JobPostingDTO> dtos) {
        log.info("Creating {} job postings", dtos.size());
        return dtos.stream()
                .map(dto -> {
                    try {
                        return createJobPosting(dto);
                    } catch (IllegalArgumentException e) {
                        log.warn("Skipping duplicate job posting with id: {}", dto.getJobPostingId());
                        return null;
                    }
                })
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Transactional
    public JobPostingDTO updateJobPosting(Long id, JobPostingDTO dto) {
        log.info("Updating job posting with id: {}", id);
        JobPostingModel model = jobPostingRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Job posting not found with id: {}", id);
                    return new IllegalArgumentException("Job posting not found");
                });

        updateModelFromDTO(model, dto);
        JobPostingModel updated = jobPostingRepository.save(model);
        log.info("Job posting updated with id: {}", updated.getId());
        return convertToDTO(updated);
    }

    @Transactional
    public void deleteJobPosting(Long id) {
        log.info("Deleting job posting with id: {}", id);
        if (!jobPostingRepository.existsById(id)) {
            log.error("Job posting not found with id: {}", id);
            throw new IllegalArgumentException("Job posting not found");
        }
        jobPostingRepository.deleteById(id);
        log.info("Job posting deleted with id: {}", id);
    }

    private JobPostingDTO convertToDTO(JobPostingModel model) {
        return JobPostingDTO.builder()
                .id(model.getId())
                .jobPostingId(model.getJobPostingId())
                .jobTitle(model.getJobTitle())
                .sourceUrl(model.getSourceUrl())
                .companyName(model.getCompanyName())
                .companyUrl(model.getCompanyUrl())
                .externalCompanyId(model.getExternalCompanyId())
                .jobLocation(model.getJobLocation())
                .employmentType(model.getEmploymentType())
                .seniorityLevel(model.getSeniorityLevel())
                .jobFunction(model.getJobFunction())
                .jobIndustries(model.getJobIndustries())
                .jobSummaryText(model.getJobSummaryText())
                .salaryCurrency(model.getSalaryCurrency())
                .salaryMin(model.getSalaryMin())
                .salaryMax(model.getSalaryMax())
                .salaryPaymentPeriod(model.getSalaryPaymentPeriod())
                .applyLink(model.getApplyLink())
                .isEasyApply(model.getIsEasyApply())
                .isApplicationAvailable(model.getIsApplicationAvailable())
                .numApplicants(model.getNumApplicants())
                .jobPosterName(model.getJobPosterName())
                .jobPosterTitle(model.getJobPosterTitle())
                .jobPostedAt(model.getJobPostedAt())
                .scrapedAt(model.getScrapedAt())
                .build();
    }

    private JobPostingModel convertToModel(JobPostingDTO dto) {
        return JobPostingModel.builder()
                .jobPostingId(dto.getJobPostingId())
                .jobTitle(dto.getJobTitle())
                .sourceUrl(dto.getSourceUrl())
                .companyName(dto.getCompanyName())
                .companyUrl(dto.getCompanyUrl())
                .externalCompanyId(dto.getExternalCompanyId())
                .jobLocation(dto.getJobLocation())
                .employmentType(dto.getEmploymentType())
                .seniorityLevel(dto.getSeniorityLevel())
                .jobFunction(dto.getJobFunction())
                .jobIndustries(dto.getJobIndustries())
                .jobSummaryText(dto.getJobSummaryText())
                .salaryCurrency(dto.getSalaryCurrency())
                .salaryMin(dto.getSalaryMin())
                .salaryMax(dto.getSalaryMax())
                .salaryPaymentPeriod(dto.getSalaryPaymentPeriod())
                .applyLink(dto.getApplyLink())
                .isEasyApply(dto.getIsEasyApply())
                .isApplicationAvailable(dto.getIsApplicationAvailable())
                .numApplicants(dto.getNumApplicants())
                .jobPosterName(dto.getJobPosterName())
                .jobPosterTitle(dto.getJobPosterTitle())
                .jobPostedAt(dto.getJobPostedAt())
                .scrapedAt(dto.getScrapedAt())
                .build();
    }

    private void updateModelFromDTO(JobPostingModel model, JobPostingDTO dto) {
        if (dto.getJobPostingId() != null) {
            model.setJobPostingId(dto.getJobPostingId());
        }
        if (dto.getJobTitle() != null) {
            model.setJobTitle(dto.getJobTitle());
        }
        if (dto.getSourceUrl() != null) {
            model.setSourceUrl(dto.getSourceUrl());
        }
        if (dto.getCompanyName() != null) {
            model.setCompanyName(dto.getCompanyName());
        }
        if (dto.getCompanyUrl() != null) {
            model.setCompanyUrl(dto.getCompanyUrl());
        }
        if (dto.getExternalCompanyId() != null) {
            model.setExternalCompanyId(dto.getExternalCompanyId());
        }
        if (dto.getJobLocation() != null) {
            model.setJobLocation(dto.getJobLocation());
        }
        if (dto.getEmploymentType() != null) {
            model.setEmploymentType(dto.getEmploymentType());
        }
        if (dto.getSeniorityLevel() != null) {
            model.setSeniorityLevel(dto.getSeniorityLevel());
        }
        if (dto.getJobFunction() != null) {
            model.setJobFunction(dto.getJobFunction());
        }
        if (dto.getJobIndustries() != null) {
            model.setJobIndustries(dto.getJobIndustries());
        }
        if (dto.getJobSummaryText() != null) {
            model.setJobSummaryText(dto.getJobSummaryText());
        }
        if (dto.getSalaryCurrency() != null) {
            model.setSalaryCurrency(dto.getSalaryCurrency());
        }
        if (dto.getSalaryMin() != null) {
            model.setSalaryMin(dto.getSalaryMin());
        }
        if (dto.getSalaryMax() != null) {
            model.setSalaryMax(dto.getSalaryMax());
        }
        if (dto.getSalaryPaymentPeriod() != null) {
            model.setSalaryPaymentPeriod(dto.getSalaryPaymentPeriod());
        }
        if (dto.getApplyLink() != null) {
            model.setApplyLink(dto.getApplyLink());
        }
        if (dto.getIsEasyApply() != null) {
            model.setIsEasyApply(dto.getIsEasyApply());
        }
        if (dto.getIsApplicationAvailable() != null) {
            model.setIsApplicationAvailable(dto.getIsApplicationAvailable());
        }
        if (dto.getNumApplicants() != null) {
            model.setNumApplicants(dto.getNumApplicants());
        }
        if (dto.getJobPosterName() != null) {
            model.setJobPosterName(dto.getJobPosterName());
        }
        if (dto.getJobPosterTitle() != null) {
            model.setJobPosterTitle(dto.getJobPosterTitle());
        }
        if (dto.getJobPostedAt() != null) {
            model.setJobPostedAt(dto.getJobPostedAt());
        }
        if (dto.getScrapedAt() != null) {
            model.setScrapedAt(dto.getScrapedAt());
        }
    }
}


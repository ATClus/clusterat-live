package com.clusterat.live.controller;

import com.clusterat.live.dto.AnalysisResponseDTO;
import com.clusterat.live.dto.JobPostingDTO;
import com.clusterat.live.service.JobPostingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/v1/job-postings")
public class JobPostingController {
    private final JobPostingService jobPostingService;

    @Autowired
    public JobPostingController(JobPostingService jobPostingService) {
        this.jobPostingService = jobPostingService;
    }

    @GetMapping
    public ResponseEntity<AnalysisResponseDTO> getAllJobPostings() {
        log.info("GET request to fetch all job postings");
        try {
            List<JobPostingDTO> jobPostings = jobPostingService.getAllJobPostings();
            return ResponseEntity.ok(AnalysisResponseDTO.builder()
                    .success(true)
                    .message("Job postings retrieved successfully")
                    .data(jobPostings)
                    .build());
        } catch (Exception e) {
            log.error("Error fetching job postings", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AnalysisResponseDTO.builder()
                            .success(false)
                            .message("Error fetching job postings")
                            .build());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnalysisResponseDTO> getJobPostingById(@PathVariable Long id) {
        log.info("GET request to fetch job posting by id: {}", id);
        try {
            Optional<JobPostingDTO> jobPosting = jobPostingService.getJobPostingById(id);
            return jobPosting.map(dto -> ResponseEntity.ok(AnalysisResponseDTO.builder()
                    .success(true)
                    .message("Job posting retrieved successfully")
                    .data(dto)
                    .build())).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(AnalysisResponseDTO.builder()
                            .success(false)
                            .message("Job posting not found")
                            .build()));
        } catch (Exception e) {
            log.error("Error fetching job posting by id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AnalysisResponseDTO.builder()
                            .success(false)
                            .message("Error fetching job posting")
                            .build());
        }
    }

    @GetMapping("/posting/{jobPostingId}")
    public ResponseEntity<AnalysisResponseDTO> getJobPostingByJobPostingId(@PathVariable String jobPostingId) {
        log.info("GET request to fetch job posting by jobPostingId: {}", jobPostingId);
        try {
            Optional<JobPostingDTO> jobPosting = jobPostingService.getJobPostingByJobPostingId(jobPostingId);
            return jobPosting.map(dto -> ResponseEntity.ok(AnalysisResponseDTO.builder()
                    .success(true)
                    .message("Job posting retrieved successfully")
                    .data(dto)
                    .build())).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(AnalysisResponseDTO.builder()
                            .success(false)
                            .message("Job posting not found")
                            .build()));
        } catch (Exception e) {
            log.error("Error fetching job posting by jobPostingId: {}", jobPostingId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AnalysisResponseDTO.builder()
                            .success(false)
                            .message("Error fetching job posting")
                            .build());
        }
    }

    @GetMapping("/company/{companyName}")
    public ResponseEntity<AnalysisResponseDTO> getJobPostingsByCompany(@PathVariable String companyName) {
        log.info("GET request to fetch job postings by company: {}", companyName);
        try {
            List<JobPostingDTO> jobPostings = jobPostingService.getJobPostingsByCompany(companyName);
            return ResponseEntity.ok(AnalysisResponseDTO.builder()
                    .success(true)
                    .message("Job postings retrieved successfully")
                    .data(jobPostings)
                    .build());
        } catch (Exception e) {
            log.error("Error fetching job postings by company: {}", companyName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AnalysisResponseDTO.builder()
                            .success(false)
                            .message("Error fetching job postings")
                            .build());
        }
    }

    @GetMapping("/location/{jobLocation}")
    public ResponseEntity<AnalysisResponseDTO> getJobPostingsByLocation(@PathVariable String jobLocation) {
        log.info("GET request to fetch job postings by location: {}", jobLocation);
        try {
            List<JobPostingDTO> jobPostings = jobPostingService.getJobPostingsByLocation(jobLocation);
            return ResponseEntity.ok(AnalysisResponseDTO.builder()
                    .success(true)
                    .message("Job postings retrieved successfully")
                    .data(jobPostings)
                    .build());
        } catch (Exception e) {
            log.error("Error fetching job postings by location: {}", jobLocation, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AnalysisResponseDTO.builder()
                            .success(false)
                            .message("Error fetching job postings")
                            .build());
        }
    }

    @GetMapping("/seniority/{seniorityLevel}")
    public ResponseEntity<AnalysisResponseDTO> getJobPostingsBySeniority(@PathVariable String seniorityLevel) {
        log.info("GET request to fetch job postings by seniority: {}", seniorityLevel);
        try {
            List<JobPostingDTO> jobPostings = jobPostingService.getJobPostingsBySeniority(seniorityLevel);
            return ResponseEntity.ok(AnalysisResponseDTO.builder()
                    .success(true)
                    .message("Job postings retrieved successfully")
                    .data(jobPostings)
                    .build());
        } catch (Exception e) {
            log.error("Error fetching job postings by seniority: {}", seniorityLevel, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AnalysisResponseDTO.builder()
                            .success(false)
                            .message("Error fetching job postings")
                            .build());
        }
    }

    @GetMapping("/employment-type/{employmentType}")
    public ResponseEntity<AnalysisResponseDTO> getJobPostingsByEmploymentType(@PathVariable String employmentType) {
        log.info("GET request to fetch job postings by employment type: {}", employmentType);
        try {
            List<JobPostingDTO> jobPostings = jobPostingService.getJobPostingsByEmploymentType(employmentType);
            return ResponseEntity.ok(AnalysisResponseDTO.builder()
                    .success(true)
                    .message("Job postings retrieved successfully")
                    .data(jobPostings)
                    .build());
        } catch (Exception e) {
            log.error("Error fetching job postings by employment type: {}", employmentType, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AnalysisResponseDTO.builder()
                            .success(false)
                            .message("Error fetching job postings")
                            .build());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<AnalysisResponseDTO> searchJobPostingsByTitle(@RequestParam String title) {
        log.info("GET request to search job postings by title: {}", title);
        try {
            List<JobPostingDTO> jobPostings = jobPostingService.searchJobPostingsByTitle(title);
            return ResponseEntity.ok(AnalysisResponseDTO.builder()
                    .success(true)
                    .message("Job postings retrieved successfully")
                    .data(jobPostings)
                    .build());
        } catch (Exception e) {
            log.error("Error searching job postings by title: {}", title, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AnalysisResponseDTO.builder()
                            .success(false)
                            .message("Error searching job postings")
                            .build());
        }
    }

    @PostMapping
    public ResponseEntity<AnalysisResponseDTO> createJobPosting(@RequestBody JobPostingDTO jobPostingDTO) {
        log.info("POST request to create job posting");
        try {
            JobPostingDTO created = jobPostingService.createJobPosting(jobPostingDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(AnalysisResponseDTO.builder()
                            .success(true)
                            .message("Job posting created successfully")
                            .data(created)
                            .build());
        } catch (IllegalArgumentException e) {
            log.error("Validation error creating job posting", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(AnalysisResponseDTO.builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Error creating job posting", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AnalysisResponseDTO.builder()
                            .success(false)
                            .message("Error creating job posting")
                            .build());
        }
    }

    @PostMapping("/bulk")
    public ResponseEntity<AnalysisResponseDTO> createJobPostings(@RequestBody List<JobPostingDTO> jobPostingDTOs) {
        log.info("POST request to create {} job postings", jobPostingDTOs.size());
        try {
            List<JobPostingDTO> created = jobPostingService.createJobPostings(jobPostingDTOs);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(AnalysisResponseDTO.builder()
                            .success(true)
                            .message(String.format("Created %d out of %d job postings",
                                    created.size(), jobPostingDTOs.size()))
                            .data(created)
                            .build());
        } catch (Exception e) {
            log.error("Error creating job postings", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AnalysisResponseDTO.builder()
                            .success(false)
                            .message("Error creating job postings")
                            .build());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnalysisResponseDTO> updateJobPosting(
            @PathVariable Long id,
            @RequestBody JobPostingDTO jobPostingDTO) {
        log.info("PUT request to update job posting with id: {}", id);
        try {
            JobPostingDTO updated = jobPostingService.updateJobPosting(id, jobPostingDTO);
            return ResponseEntity.ok(AnalysisResponseDTO.builder()
                    .success(true)
                    .message("Job posting updated successfully")
                    .data(updated)
                    .build());
        } catch (IllegalArgumentException e) {
            log.error("Validation error updating job posting", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(AnalysisResponseDTO.builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Error updating job posting with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AnalysisResponseDTO.builder()
                            .success(false)
                            .message("Error updating job posting")
                            .build());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AnalysisResponseDTO> deleteJobPosting(@PathVariable Long id) {
        log.info("DELETE request to delete job posting with id: {}", id);
        try {
            jobPostingService.deleteJobPosting(id);
            return ResponseEntity.ok(AnalysisResponseDTO.builder()
                    .success(true)
                    .message("Job posting deleted successfully")
                    .build());
        } catch (IllegalArgumentException e) {
            log.error("Validation error deleting job posting", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(AnalysisResponseDTO.builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Error deleting job posting with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AnalysisResponseDTO.builder()
                            .success(false)
                            .message("Error deleting job posting")
                            .build());
        }
    }
}


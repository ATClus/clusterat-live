package com.clusterat.live.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "job_postings", schema = "live")
public class JobPostingModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_posting_id", nullable = false, length = 50, unique = true)
    private String jobPostingId;

    @Column(name = "job_title", nullable = false)
    private String jobTitle;

    @Column(name = "source_url", nullable = false, columnDefinition = "TEXT")
    private String sourceUrl;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "company_url", columnDefinition = "TEXT")
    private String companyUrl;

    @Column(name = "external_company_id", length = 50)
    private String externalCompanyId;

    @Column(name = "job_location")
    private String jobLocation;

    @Column(name = "employment_type", length = 100)
    private String employmentType;

    @Column(name = "seniority_level", length = 100)
    private String seniorityLevel;

    @Column(name = "job_function")
    private String jobFunction;

    @Column(name = "job_industries")
    private String jobIndustries;

    @Column(name = "job_summary_text", columnDefinition = "TEXT")
    private String jobSummaryText;

    @Column(name = "salary_currency", length = 10)
    private String salaryCurrency;

    @Column(name = "salary_min", precision = 12, scale = 2)
    private BigDecimal salaryMin;

    @Column(name = "salary_max", precision = 12, scale = 2)
    private BigDecimal salaryMax;

    @Column(name = "salary_payment_period", length = 50)
    private String salaryPaymentPeriod;

    @Column(name = "apply_link", columnDefinition = "TEXT")
    private String applyLink;

    @Column(name = "is_easy_apply")
    private Boolean isEasyApply;

    @Column(name = "is_application_available")
    private Boolean isApplicationAvailable;

    @Column(name = "num_applicants")
    private Short numApplicants;

    @Column(name = "job_poster_name")
    private String jobPosterName;

    @Column(name = "job_poster_title")
    private String jobPosterTitle;

    @Column(name = "job_posted_at")
    private OffsetDateTime jobPostedAt;

    @Column(name = "scraped_at", nullable = false)
    private OffsetDateTime scrapedAt;

    @PrePersist
    protected void onCreate() {
        if (scrapedAt == null) {
            scrapedAt = OffsetDateTime.now();
        }
        if (isEasyApply == null) {
            isEasyApply = false;
        }
        if (isApplicationAvailable == null) {
            isApplicationAvailable = true;
        }
    }
}

